package com.loadburn.heron.route;

import com.google.common.collect.*;
import com.google.inject.*;
import com.loadburn.heron.annotations.*;
import com.loadburn.heron.bind.Request;
import com.loadburn.heron.excetions.InvalidMethodException;
import com.loadburn.heron.excetions.RouteDispatchException;
import com.loadburn.heron.render.Renderable;
import com.loadburn.heron.transport.Form;
import com.loadburn.heron.transport.Transport;
import com.loadburn.heron.utils.StringUtils;
import com.loadburn.heron.utils.converter.TypeConverter;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.Nullable;

import javax.validation.ValidationException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-26
 */
@ThreadSafe
@Singleton
public class DefaultHeronPage implements HeronPage {

    @GuardedBy("lock") // All three following fields
    private final Map<String, List<PageAtom>> pages = Maps.newHashMap();
    private final List<PageAtom> universalMatchingPages = Lists.newArrayList();
    private final Map<String, PageAtom> pagesByName = Maps.newHashMap();
    private final Set<HeronPage.Page> pagesAtomSet = Sets.newHashSet();

    private final ConcurrentMap<Class<?>, PageAtom> classToPageMap =
            new MapMaker()
                    .weakKeys()
                    .weakValues()
                    .makeMap();

    private final Object lock = new Object();
    private final Injector injector;

    private Map<String, Class<? extends Annotation>> httpMethods;

    private static final Key<Map<String, Class<? extends Annotation>>> HTTP_METHODS_KEY =
            Key.get(new TypeLiteral<Map<String, Class<? extends Annotation>>>() {
            }, Heron.class);

    @Inject
    public DefaultHeronPage(Injector injector) {
        this.injector = injector;
    }

    String firstPathElement(String uri) {
        String shortUri = uri.substring(1);

        final int index = shortUri.indexOf("/");

        return (index >= 0) ? shortUri.substring(0, index) : shortUri;
    }

    private static boolean isVariable(String key) {
        return key.length() > 0 && ':' == key.charAt(0);
    }


    private String getValue(Class<? extends Annotation> get, Method method) {
        return readAnnotationValue(method.getAnnotation(get));
    }

    static String readAnnotationValue(Annotation annotation) {
        try {
            Method m = annotation.getClass().getMethod("value");

            return (String) m.invoke(annotation);

        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Encountered a configured http that " +
                    "has no value parameter. This should never happen. " + annotation, e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Encountered a configured http that " +
                    "could not be read." + annotation, e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Encountered a configured http that " +
                    "could not be read." + annotation, e);
        }
    }

    @Override
    public Set<HeronPage.Page> path(String uri, Class<?> pageClass) {
        for (Method method : pageClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Path.class)) {

                // This is a subpath expression.
                Path at = method.getAnnotation(Path.class);
                String subpath = at.value();

                // Validate subpath
                if (!subpath.startsWith("/") || subpath.isEmpty() || subpath.length() == 1) {
                    throw new IllegalArgumentException(String.format(
                            "Subpath Path(\"%s\") on %s.%s() must begin with a \"/\" and must not be empty",
                            subpath, pageClass.getName(), method.getName()));
                }

                subpath = uri + subpath;

                // Register as headless web service.
                doPath(subpath, pageClass);
            }
        }

        return pagesAtomSet;
    }

    private PageAtom doPath(String uri, Class<?> clazz) {

        final String key = firstPathElement(uri);
        Decoration decoration = clazz.getAnnotation(Decoration.class);
        PageAtom pageAtom = null;
        if (decoration != null) {
            pageAtom = new PageAtom(uri, new PathMatcherDefault(uri), clazz, injector, true);
        } else {
            pageAtom = new PageAtom(uri, new PathMatcherDefault(uri), clazz, injector);
        }

        synchronized (lock) {
            //is universal? (i.e. first element is a variable)
            if (isVariable(key))
                universalMatchingPages.add(pageAtom);
            else {
                multiput(pages, key, pageAtom);
            }
        }

        // Does not need to be inside lock, as it is concurrent.
        classToPageMap.put(clazz, pageAtom);
        pagesAtomSet.add(pageAtom);

        return pageAtom;
    }

    private static void multiput(Map<String, List<PageAtom>> pages, String key,
                                 PageAtom page) {
        List<PageAtom> list = pages.get(key);

        if (null == list) {
            list = new ArrayList<PageAtom>();
            pages.put(key, list);
        }

        list.add(page);
    }

    @Override
    @Nullable
    public Page get(String uri) {
        final String key = firstPathElement(uri);

        List<PageAtom> tuple = pages.get(key);

        //first try static first piece
        if (null != tuple) {

            //first try static first piece
            for (PageAtom pageTuple : tuple) {
                if (pageTuple.matcher.matches(uri))
                    return pageTuple;
            }
        }

        //now try dynamic first piece (how can we make this faster?)
        for (PageAtom pageTuple : universalMatchingPages) {
            if (pageTuple.matcher.matches(uri))
                return pageTuple;
        }

        //nothing matched
        return null;
    }

    @Override
    public Page forName(String name) {
        return pagesByName.get(name);
    }

    @Override
    public Page forInstance(Object instance) {
        Class<?> aClass = instance.getClass();
        PageAtom targetType = classToPageMap.get(aClass);

        // Do a super crawl to detect the target type.
        while (null == targetType) {
            aClass = aClass.getSuperclass();
            targetType = classToPageMap.get(aClass);

            // Stop path the root =D
            if (Object.class.equals(aClass)) {
                return null;
            }
        }

        return PageDelegate.delegating(targetType, instance);
    }

    @Override
    public Page forClass(Class<?> pageClass) {
        return classToPageMap.get(pageClass);
    }

    @Override
    public Collection<List<Page>> getPageMap() {
        return (Collection) pages.values();
    }

    @Override
    public Page decorate(Class<?> pageClass) {
        PageAtom pageAtom = new PageAtom("", PathMatcherDefault.ignoring(), pageClass, injector, true);
        String name = pageClass.getName().toLowerCase() + "-extend";
        synchronized (lock) {
            pagesByName.put(name, pageAtom);
        }
        return pageAtom;
    }

    public static class PageDelegate implements Page {

        private final Page delegate;
        private final Object instance;

        public PageDelegate(Page delegate, Object instance) {
            this.delegate = delegate;
            this.instance = instance;
        }

        public static Page delegating(Page delegate, Object instance) {
            return new PageDelegate(delegate, instance);
        }

        @Override
        public Object instantiate() {
            return instance;
        }

        @Override
        public Object doMethod(String httpMethod, Object page, String pathInfo, Request request) throws IOException {
            return delegate.doMethod(httpMethod, page, pathInfo, request);
        }

        @Override
        public Class<?> pageClass() {
            return delegate.pageClass();
        }

        @Override
        public String getUri() {
            return delegate.getUri();
        }

        @Override
        public Set<String> getMethod() {
            return delegate.getMethod();
        }

        @Override
        public Show getShow() {
            return delegate.getShow();
        }

        @Override
        public Renderable widget() {
            return delegate.widget();
        }

        @Override
        public void apply(Renderable widget) {
            delegate.apply(widget);
        }

        @Override
        public boolean isDecorated() {
            return delegate.isDecorated();
        }

        @Override
        public int compareTo(Page page) {
            return delegate.compareTo(page);
        }
    }

    public static class PageAtom implements Page {
        private final String uri;
        private final PathMatcher matcher;
        private final AtomicReference<Renderable> pageWidget = new AtomicReference<Renderable>();
        private final Class<?> clazz;
        private final Injector injector;
        private final boolean extension;

        private final Multimap<String, Action> methods;

        private static final Key<Map<String, Class<? extends Annotation>>> HTTP_METHODS_KEY =
                Key.get(new TypeLiteral<Map<String, Class<? extends Annotation>>>() {
                }, Heron.class);

        private Map<String, Class<? extends Annotation>> httpMethods;

        public PageAtom(String uri, PathMatcher matcher, Class<?> clazz,
                        Injector injector, Multimap<String, Action> methods) {
            this.uri = uri;
            this.matcher = matcher;
            this.clazz = clazz;
            this.injector = injector;
            this.methods = methods;
            this.extension = false;
            this.httpMethods = injector.getInstance(HTTP_METHODS_KEY);
        }

        public PageAtom(String uri, PathMatcher matcher, Class<?> clazz, Injector injector) {
            this.uri = uri;
            this.matcher = matcher;
            this.clazz = clazz;
            this.injector = injector;
            this.extension = false;
            this.httpMethods = injector.getInstance(HTTP_METHODS_KEY);
            this.methods = reflectAndCache(uri, httpMethods);
        }

        public PageAtom(String uri, PathMatcher matcher, Class<?> clazz, Injector injector, boolean extension) {
            this.uri = uri;
            this.matcher = matcher;
            this.clazz = clazz;
            this.injector = injector;
            this.extension = extension;

            this.httpMethods = injector.getInstance(HTTP_METHODS_KEY);
            this.methods = reflectAndCache(uri, httpMethods);
        }

        @SuppressWarnings({"JavaDoc"})
        private Multimap<String, Action> reflectAndCache(String uri,
                                                         Map<String, Class<? extends Annotation>> methodMap) {
            String tail = "";
            if (clazz.isAnnotationPresent(Path.class)) {
                int length = clazz.getAnnotation(Path.class).value().length();

                if (uri != null && length <= uri.length())
                    tail = uri.substring(length);
            }

            Multimap<String, Action> map = HashMultimap.create();

            for (Map.Entry<String, Class<? extends Annotation>> entry : methodMap.entrySet()) {

                Class<? extends Annotation> get = entry.getValue();
                for (Method method : clazz.getMethods()) {
                    if (method.isAnnotationPresent(get)) {
                        if (!method.isAccessible())
                            method.setAccessible(true); //ugh

                        if (method.isAnnotationPresent(Path.class)) {
                            if (tail.isEmpty()) {
                                continue;
                            }
                            if (!tail.equals(method.getAnnotation(Path.class).value())) {
                                continue;
                            }

                            String key = entry.getKey();
                            map.put(key, new MethodAction(method, injector));

                        } else if (!tail.isEmpty()) {
                            continue;
                        }
                    }
                }

                // Then search class's declared methods only (these take precedence)
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(get)) {
                        if (!method.isAccessible())
                            method.setAccessible(true); //ugh

                        if (method.isAnnotationPresent(Path.class)) {
                            if (tail.isEmpty()) {
                                continue;
                            }
                            if (!tail.equals(method.getAnnotation(Path.class).value())) {
                                continue;
                            }
                            String key = entry.getKey();
                            map.put(key, new MethodAction(method, injector));

                        } else if (!tail.isEmpty()) {
                            continue;
                        }
                    }
                }
            }

            return map;
        }

        public Object instantiate() {
            return clazz == null ? Collections.emptyMap() : injector.getInstance(clazz);
        }

        public Set<String> getMethod() {
            return methods.keySet();
        }

        public int compareTo(Page page) {
            return uri.compareTo(page.getUri());
        }

        public Object doMethod(String httpMethod, Object page, String pathInfo,
                               Request request) throws IOException {

            if (StringUtils.empty(httpMethod)) {
                return null;
            }

            Multimap<String, String> params = request.params();

            final Map<String, String> map = matcher.findMatches(pathInfo);

            boolean matched = false;
            String key = httpMethod;
            Collection<Action> tuples = methods.get(key);
            Object redirect = null;

            if (null != tuples) {
                for (Action action : tuples) {
                    if (action.canCall(request)) {
                        matched = true;
                        redirect = action.call(request, page, map);
                        break;
                    }
                }
            }

            if (null != redirect) {
                return redirect;
            }

            if (!matched) {
                return callAction(httpMethod, page, map, request);
            }

            return null;
        }

        private Object callAction(String httpMethod, Object page, Map<String, String> pathMap,
                                  Request request) throws IOException {

            Collection<Action> tuple = methods.get(httpMethod);
            Object redirect = null;
            if (null != tuple) {
                for (Action action : tuple) {
                    if (action.canCall(request)) {
                        redirect = action.call(request, page, pathMap);
                        break;
                    }
                }
            }
            return redirect;
        }

        public Class<?> pageClass() {
            return clazz;
        }

        public void apply(Renderable widget) {
            this.pageWidget.set(widget);
        }

        @Override
        public boolean isDecorated() {
            return extension;
        }

        public String getUri() {
            return uri;
        }

        @Override
        public Show getShow() {
            for (String httpMethod : methods.keySet()) {
                Collection<Action> actions = methods.get(httpMethod);
                if (actions != null) {
                    for (Action action : actions) {
                        Method method = action.getMethod();
                        if (method != null) {
                            Show show = action.getMethod().getAnnotation(Show.class);
                            if (show != null) {
                                return show;
                            }
                        }
                    }
                }
            }
            return null;
        }

        @Override
        public Renderable widget() {
            return pageWidget.get();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Page)) return false;

            Page that = (Page) o;

            return this.getUri().equals(that.getUri());
        }

        @Override
        public int hashCode() {
            return clazz.hashCode();
        }

        @Override
        public String toString() {
            return com.google.common.base.Objects.toStringHelper(PageAtom.class).add("clazz", clazz)
                    .add("uri", uri).add("methods", methods).toString();
        }

    }

    private static class MethodAction implements Action {
        private final Method method;
        private final Injector injector;
        private final List<Object> args;
        private final TypeConverter converter;
        private final As returnAs;

        private MethodAction(Method method, Injector injector) {
            this.method = method;
            this.injector = injector;
            this.args = reflect(method);
            this.converter = injector.getInstance(TypeConverter.class);
            this.returnAs = method.getAnnotation(As.class);
        }

        private List<Object> reflect(Method method) {

            final Annotation[][] annotationsGrid = method.getParameterAnnotations();

            if (null == annotationsGrid)
                return Collections.emptyList();

            List<Object> args = new ArrayList<Object>();

            for (int i = 0; i < annotationsGrid.length; i++) {

                Annotation[] annotations = annotationsGrid[i];

                Annotation bindingAnnotation = null;
                boolean preInjectableFound = false;
                for (Annotation annotation : annotations) {
                    if (PathVariable.class.isInstance(annotation)) {
                        PathVariable named = (PathVariable) annotation;
                        args.add(new NamedParameter(named.value(), method.getGenericParameterTypes()[i]));
                        preInjectableFound = true;
                        break;
                    } else if (As.class.isInstance(annotation)) {
                        As as = (As) annotation;
                        if (method.isAnnotationPresent(Get.class)) {
                            if (!as.value().equals(Form.class)) {
                                throw new IllegalArgumentException("@As注解不能和@Get混用，只能和@Post一起使用: "
                                        + method.getDeclaringClass().getName() + "#" + method.getName() + "()");
                            }
                        }

                        preInjectableFound = true;
                        args.add(new AsParameter(as.value(), TypeLiteral.get(method.getGenericParameterTypes()[i])));
                        break;
                    }
                    if (Require.class.isInstance(annotation)) {
                        Require require = (Require) annotation;
                        args.add(Key.get(method.getGenericParameterTypes()[i]));
                        preInjectableFound = true;
                        break;
                    }
                }

                if (!preInjectableFound) {

                    Type genericParameterType = method.getGenericParameterTypes()[i];

                    Key<?> key = (null != bindingAnnotation)
                            ? Key.get(genericParameterType, bindingAnnotation)
                            : Key.get(genericParameterType);

                    args.add(key);

                    if (null == injector.getBindings().get(key)) {

                        throw new InvalidMethodException(
                                "方法有没用注解或者@Name修饰的参数: " + method + " " + key);

                    }

                }

            }

            return Collections.unmodifiableList(args);
        }

        @Override
        public boolean canCall(Request request) {
            return true;
        }

        @Override
        public Object call(Request request, Object page, Map<String, String> map) throws IOException {
            List<Object> arguments = new ArrayList<Object>();
            for (Object arg : args) {
                if (arg instanceof AsParameter) {
                    AsParameter as = (AsParameter) arg;
                    arguments.add(request.read(as.type).as(as.transport));
                } else if (arg instanceof NamedParameter) {
                    NamedParameter np = (NamedParameter) arg;
                    String text = map.get(np.getName());
                    Object value = converter.convert(text, np.getType());
                    arguments.add(value);
                } else
                    arguments.add(injector.getInstance((Key<?>) arg));
            }

            Object result = call(page, method, arguments.toArray());
            return result;
        }

        @Override
        public Method getMethod() {
            return this.method;
        }

        private static Object call(Object page, final Method method,
                                   Object[] args) {
            try {
                return method.invoke(page, args);
            } catch (IllegalAccessException e) {
                throw new RouteDispatchException(
                        "不能访问方法(可能是安全问题): " + method, e);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof ValidationException) {
                    throw (ValidationException) cause;
                }
                StackTraceElement[] stackTrace = cause.getStackTrace();
                throw new RouteDispatchException(String.format(
                        "Exception [%s - \"%s\"] thrown by event method [%s]\n\npath %s\n"
                                + "(See below for entire trace.)\n",
                        cause.getClass().getSimpleName(),
                        cause.getMessage(), method,
                        stackTrace[0]), e);
            }
        }

        public class NamedParameter {
            private final String name;
            private final Type type;

            public NamedParameter(String name, Type type) {
                this.name = name;
                this.type = type;
            }

            public String getName() {
                return name;
            }

            public Type getType() {
                return type;
            }
        }

        public class AsParameter {
            private final Class<? extends Transport> transport;
            private final TypeLiteral<?> type;

            public AsParameter(Class<? extends Transport> transport, TypeLiteral<?> type) {
                this.transport = transport;
                this.type = type;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MethodAction that = (MethodAction) o;

            if (!method.equals(that.method)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return method.hashCode();
        }

        @Override
        public String toString() {
            return "MethodAction [method=" + method + ", args=" + args + "]";
        }

    }
}
