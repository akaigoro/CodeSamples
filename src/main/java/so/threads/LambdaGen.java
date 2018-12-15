package so.threads;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Supplier;

public class LambdaGen {

    static class TypeToken<T> {

        private final Type type;
        private final Class<? extends TypeToken> rawType;

        /* ==== Constructor ==== */

        @SuppressWarnings("unchecked")
        protected TypeToken() {
            rawType = this.getClass();
            ParameterizedType paramType = (ParameterizedType) rawType.getGenericSuperclass();
            this.type = paramType.getActualTypeArguments()[0];
        }

        void printType() {
            if (type==null) {
                System.out.println("type=null");
            } else if (type instanceof GenericArrayType){
                GenericArrayType type1 = (GenericArrayType) type;
                Class<? extends GenericArrayType> aClass = type1.getClass();
                Type compType = type1.getGenericComponentType();
                System.out.println("type="+type+":"+ aClass+": compType="+compType);
            } else {
                Class<? extends Type> aClass = type.getClass();
                System.out.println("type="+type+":"+ aClass);
            }
        }
    }

    static<T> TypeToken<T[]> getTypeToken() {
        return new TypeToken<T[]>() {};
    }

    static <T> void test() {
        TypeToken<T[]> token = new TypeToken<T[]>() {};
        token.printType();

        Supplier<TypeToken<T[]>> sup2 = new Supplier<TypeToken<T[]>>() {
            @Override
            public TypeToken<T[]> get() {
                return new TypeToken<T[]>() {};
            }
        };
        sup2.get().printType();

        getTypeToken().printType();

        Supplier<TypeToken<T[]>> sup3 = () -> getTypeToken();
        sup3.get().printType();

        Supplier<TypeToken<T[]>> sup4 = () -> new TypeToken<T[]>() {};
        sup4.get().printType();
    }

    public static void main(String[] args) {
        TypeToken<List<String>> token = new TypeToken<List<String>>() {};
        token.printType();
        TypeToken<List<? extends CharSequence>> token2 = new TypeToken<List<? extends CharSequence>>() {};
        token.printType();

        LambdaGen.<Double>test();
    }

}
