package com.loadburn.heron.utils.generics;

import java.lang.reflect.Type;

interface CaptureType extends Type {

    Type[] getUpperBounds();

    Type[] getLowerBounds();

}