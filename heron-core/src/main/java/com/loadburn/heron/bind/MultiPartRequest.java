package com.loadburn.heron.bind;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.loadburn.heron.validation.HeronValidator;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-25
 */
public class MultiPartRequest implements Request<FileItem> {

    private final HttpServletRequest httpServletRequest;

    private HeronValidator validator;

    private Multimap<String, FileItem> params;

    @Inject
    public MultiPartRequest(Provider<HttpServletRequest> requestProvider, HeronValidator validator) throws FileUploadException {
        this.httpServletRequest = requestProvider.get();
        this.validator = validator;
        this.params = params(this.httpServletRequest);
    }

    private Multimap<String, FileItem> params(HttpServletRequest request) throws FileUploadException {

        ImmutableMultimap.Builder<String, FileItem> builder = ImmutableMultimap.builder();
        FileItemFactory fileItemFactory = new DiskFileItemFactory(1000, null);

        ServletFileUpload upload = new ServletFileUpload(fileItemFactory);
        upload.setHeaderEncoding(request.getCharacterEncoding());
        List<FileItem> items = upload.parseRequest(request);

        Iterator<FileItem> iter = items.iterator();
        while (iter.hasNext()) {
            FileItem fileItem = (FileItem) iter.next();
            builder.put(fileItem.getFieldName(), fileItem);
        }

        return builder.build();

    }


    @Override
    public <E> RequestRead<E> read(Class<E> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E> RequestRead<E> read(TypeLiteral<E> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void readTo(OutputStream out) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Multimap<String, String> headers() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Multimap<String, String> matrix() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String matrixParam(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String header(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String uri() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String path() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String completePath() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String context() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String method() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void validate(Object object) {
        Set<? extends ConstraintViolation<?>> cvs = validator.validate(object);
        if ((cvs != null) && (!cvs.isEmpty())) {
            throw new ValidationException(new ConstraintViolationException((Set<ConstraintViolation<?>>) cvs));
        }
    }

    @Override
    public FileItem param(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Multimap<String, FileItem> params() {
        return this.params;
    }
}
