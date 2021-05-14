package h03;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.opentest4j.TestAbortedException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Utils {

    public static final long seed = new Random().nextLong();
    public static final Random random = new Random(seed);
    public static final IntFunction<Character> CAST_TO_CHARACTER = n -> (char) n;
    public static final List<Object> alphabet = IntStream.range(0, 26).mapToObj(i -> (char) ('A' + i)).collect(Collectors.toUnmodifiableList());

    public static Class<?> getClassForName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new TestAbortedException("Class " + e.getMessage() + " not found", e);
        }
    }

    public static Object functionToIntProxyForAlphabet(List<Object> alphabet) throws ReflectiveOperationException {
        Class<?> functionToIntClass = Class.forName("h03.FunctionToInt");
        InvocationHandler handler = new InvocationHandler() {
            @Override
            @SuppressWarnings("SuspiciousInvocationHandlerImplementation")
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.equals(functionToIntClass.getMethod("apply", Object.class)))
                    return alphabet.indexOf(args[0]);
                else if (method.equals(functionToIntClass.getMethod("sizeOfAlphabet")))
                    return alphabet.size();

                throw new NoSuchMethodException(method.toString());
            }
        };

        return Proxy.newProxyInstance(functionToIntClass.getClassLoader(), new Class[] {functionToIntClass}, handler);
    }

    public static String randomLatinString(int size) {
        return IntStream
                .generate(() -> 'A' + random.nextInt(26))
                .mapToObj(CAST_TO_CHARACTER)
                .map(String::valueOf)
                .limit(size)
                .collect(Collectors.joining());
    }

    public static Character[] stringAsArray(String s) {
        return s.chars()
                .mapToObj(CAST_TO_CHARACTER)
                .toArray(Character[]::new);
    }

    @Test
    public void printSeed() {
        System.out.println("Seed: " + seed);
    }

    private static final Map<Class<?>, Boolean> defCorrect = new HashMap<>();

    public static boolean definitionCorrect(Class<?> c) {
        if (defCorrect.containsKey(c))
            return defCorrect.get(c);

        try {
            c.getDeclaredMethod(c.getDeclaredAnnotation(DefinitionCheck.class).value()).invoke(null);
            defCorrect.put(c, true);
        } catch (Exception e) {
            defCorrect.put(c, false);
        }

        return defCorrect.get(c);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface DefinitionCheck {
        String value();
    }
}

class RandomCharProvider implements ArgumentsProvider {

    private static final int numberOfRandomArguments = 3;

    /**
     * Returns a stream of arguments with format {@code (char as integer, char)}.
     * The stream will always contain arguments for the lower and upper bound of {@link Character}.
     * If {@link RandomCharProvider#numberOfRandomArguments} is greater than zero then the stream will
     * contain that amount of arguments with random characters. So the total amount of elements
     * in the stream will be 2 + numberOfRandomArguments
     * @param context the context supplied by JUnit, may be null when invoking directly
     * @return the stream of arguments
     */
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.concat(
                Stream.of(Arguments.of(0, '\u0000'), Arguments.of(65535, '\uFFFF')),
                new Random(Utils.seed)
                        .ints(numberOfRandomArguments, 1, 65535)
                        .mapToObj(i -> Arguments.of(i, (char) i))
        );
    }
}

class RandomNeedleProvider implements ArgumentsProvider {

    private static final int numberOfRandomArguments = 5;

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        String[] needles = new String[numberOfRandomArguments];

        for (int i = 0; i < needles.length; i++)
            needles[i] = Utils.randomLatinString(10);

        return Arrays.stream(needles).map(s -> {
            int repeatLength = Utils.random.nextInt(s.length());

            return Arguments.of(
                    Utils.stringAsArray(s + s.substring(0, repeatLength)),
                    repeatLength
            );
        });
    }
}

class RandomMatcherArgumentsProvider implements ArgumentsProvider {

    private static final int STACK_SIZE = 20;
    private static final double NEEDLE_FREQUENCY = 0.1;
    private static final double OVERLAP_FREQUENCY = 0.2;

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return new RandomNeedleProvider().provideArguments(null).map(arguments -> {
            List<Character> needle = Arrays.stream(((Character[]) arguments.get()[0])).collect(Collectors.toList()),
                            stack = Utils.randomLatinString(STACK_SIZE)
                                         .chars()
                                         .mapToObj(Utils.CAST_TO_CHARACTER)
                                         .map(character -> {
                                             if (Utils.random.nextDouble() < NEEDLE_FREQUENCY)
                                                 return Utils.random.nextDouble() < OVERLAP_FREQUENCY ? '#' : '$';
                                             else
                                                 return character;
                                         })
                                         .collect(Collectors.toList());
            List<Integer> matchIndices = new ArrayList<>();
            int repeatLength = (int) arguments.get()[1];

            for (int i = 0; i < stack.size(); i++)
                switch (stack.get(i)) {
                    case '$':
                        stack.set(i, needle.get(0));
                        stack.addAll(i + 1, new ArrayList<>(needle.subList(1, needle.size())));

                        matchIndices.add(i + 1); // + 1 because "convention"
                        break;

                    case '#':
                        List<Character> overlappingNeedle = new ArrayList<>(needle.subList(1, needle.size() - repeatLength));
                        overlappingNeedle.addAll(new ArrayList<>(needle));

                        stack.set(i, needle.get(0));
                        stack.addAll(i + 1, overlappingNeedle);

                        matchIndices.add(i + 1); // + 1 because "convention"
                        matchIndices.add(i + needle.size() - repeatLength + 1); // and here as well
                }

            return Arguments.of(stack, needle, matchIndices);
        });
    }
}
