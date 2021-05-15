package h03;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.reflect.*;
import java.util.*;

import static h03.Utils.*;
import static java.lang.reflect.Modifier.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@DefinitionCheck("checkClass")
class PartialMatchLengthUpdateValuesAsMatrixTest {

    static Class<?> partialMatchLengthUpdateValuesAsMatrixClass;
    static Constructor<?> constructor;
    static Method getPartialMatchLengthUpdate;
    static Field lookupTable;

    @BeforeAll
    public static void checkClass() {
        assumeTrue(definitionCorrect(FunctionToIntTest.class),
                "PartialMatchLengthUpdateValuesAsMatrix requires that interface FunctionToInt is implemented correctly");
        assumeTrue(definitionCorrect(PartialMatchLengthUpdateValuesTest.class),
                "PartialMatchLengthUpdateValuesAsMatrix requires that class PartialMatchLengthUpdateValues is implemented correctly");

        try {
            partialMatchLengthUpdateValuesAsMatrixClass = Class.forName("h03.PartialMatchLengthUpdateValuesAsMatrix");
        } catch (ClassNotFoundException e) {
            assumeTrue(false, "Class " + e.getMessage() + " not found");
        }

        // is generic
        TypeVariable<?>[] typeParameters = partialMatchLengthUpdateValuesAsMatrixClass.getTypeParameters();

        assertEquals(1, typeParameters.length, "PartialMatchLengthUpdateValuesAsMatrix must be generic");
        assertEquals("T", typeParameters[0].getName(), "Type parameter of class PartialMatchLengthUpdateValuesAsMatrix is not named 'T'");

        // extends PartialMatchLengthUpdateValues<T>
        assertEquals(
                "h03.PartialMatchLengthUpdateValues<T>",
                partialMatchLengthUpdateValuesAsMatrixClass.getGenericSuperclass().getTypeName(),
                "PartialMatchLengthUpdateValuesAsMatrix must extend PartialMatchLengthUpdateValues"
        );

        // is not abstract
        assertFalse(
                isAbstract(partialMatchLengthUpdateValuesAsMatrixClass.getModifiers()),
                "PartialMatchLengthUpdateValuesAsMatrix must not be abstract"
        );

        // fields
        for (Field field : partialMatchLengthUpdateValuesAsMatrixClass.getDeclaredFields())
            if (isPrivate(field.getModifiers()) && field.getType().equals(int[][].class) && lookupTable == null)
                lookupTable = field;

        assertNotNull(lookupTable, "Class PartialMatchLengthUpdateValuesAsMatrix has no field matching the criteria for the lookup table");

        lookupTable.setAccessible(true);

        // constructors
        try {
            constructor = partialMatchLengthUpdateValuesAsMatrixClass.getDeclaredConstructor(FunctionToIntTest.functionToIntIntf, Object[].class);
        } catch (NoSuchMethodException e) {
            fail("PartialMatchLengthUpdateValuesAsMatrix is missing a required constructor", e);
        }

        // methods
        try {
            getPartialMatchLengthUpdate = partialMatchLengthUpdateValuesAsMatrixClass.getDeclaredMethod("getPartialMatchLengthUpdate", int.class, Object.class);
        } catch (NoSuchMethodException e) {
            fail("PartialMatchLengthUpdateValuesAsMatrix is missing required method \"getPartialMatchLengthUpdate(int, T)\"", e);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(RandomNeedleProvider.class)
    public void testLookupTable(Character[] needle, int repeatLength) throws ReflectiveOperationException {
        Object functionToIntInstance = functionToIntProxyForAlphabet(alphabet);
        Object instance = constructor.newInstance(functionToIntInstance, needle);
        MatrixHandler matrix = new MatrixHandler(partialMatchLengthUpdateValuesAsMatrixClass, instance, lookupTable);

        // dimensions of lookup table
        assertEquals(needle.length + 1, matrix.getNumberOfRows());
        assertEquals(FunctionToIntTest.sizeOfAlphabet.invoke(functionToIntInstance), matrix.getNumberOfColumns());

        // check that every value in column of first character is > 0
        for (int i = 0, valueOfFirstCharacter = (int) FunctionToIntTest.apply.invoke(functionToIntInstance, needle[0]); i < needle.length + 1; i++)
            assertTrue(
                    matrix.get(i, valueOfFirstCharacter) > 0,
                    "Values in column apply(needle[0]) (or row if alphabet is assigned to rows) have to be at least 1"
            );

        // check that values for needle elements are correct
        for (int i = 0; i < needle.length; i++)
            assertEquals(
                    i + 1,
                    matrix.get(i, (int) FunctionToIntTest.apply.invoke(functionToIntInstance, needle[i])),
                    "Value for apply(needle[i]) in row i (or column i if alphabet is assigned to rows) should be i + 1"
            );

        // check... idk how to describe it, involves computePartialMatchLengthUpdateValues
        assertEquals(
                repeatLength + 1,
                matrix.get(needle.length, (int) FunctionToIntTest.apply.invoke(functionToIntInstance, needle[repeatLength])),
                "The value at index apply(needle[repeatLength]) in the last row of the lookup table " +
                        "(or vice versa if alphabet is assigned to rows) does not equal the expected value. " +
                        "Take a look at the summary for the string matching BOFA algorithm in moodle"
        );

        // check that other values are 0
        for (int i = 0; i < needle.length + 1; i++)
            for (int j = 0; j < alphabet.size(); j++)
                if (!matrix.checkedCoordinates.contains(new MatrixHandler.Coordinates(i, j)))
                    assertEquals(0, matrix.get(i, j, false), "Should be 0");
    }

    @ParameterizedTest
    @ArgumentsSource(RandomNeedleProvider.class)
    public void testGetPartialMatchLengthUpdate(Character[] needle, @SuppressWarnings("unused") int repeatLength) throws ReflectiveOperationException {
        Object functionToIntInstance = functionToIntProxyForAlphabet(alphabet);
        Object instance = constructor.newInstance(functionToIntInstance, needle);

        // check that method returns correct values for match
        for (int i = 0; i < needle.length; i++)
            assertEquals(
                    i + 1,
                    getPartialMatchLengthUpdate.invoke(instance, i, needle[i]),
                    "getPartialMatchLengthUpdate(i, needle[i]) should return i + 1"
            );

        // the method is rather trivial to implement, the correctness of the lookup table is more important
    }

    private static class MatrixHandler {

        private final int[][] matrix;
        private final boolean transpose;

        public final Set<Coordinates> checkedCoordinates;

        public MatrixHandler(Class<?> c, Object instance, Field field) throws ReflectiveOperationException {
            matrix = ((int[][]) field.get(instance));
            checkedCoordinates = new HashSet<>(matrix.length * matrix[0].length);

            // determine whether the lookup table has alphabet assigned to rows or columns
            List<Object> alphabet = List.of('A');
            Object cInstance = c.getDeclaredConstructor(Class.forName("h03.FunctionToInt"), Object[].class)
                    .newInstance(functionToIntProxyForAlphabet(alphabet), new Object[] {'A', 'A', 'A'});
            transpose = ((int[][]) field.get(cInstance)).length == alphabet.size();
        }

        public int get(int row, int col) {
            return get(row, col, true);
        }

        public int get(int row, int col, boolean addToCoords) {
            if (addToCoords)
                checkedCoordinates.add(new Coordinates(row, col));

            return matrix[transpose ? col : row][transpose ? row : col];
        }

        public int getNumberOfRows() {
            return transpose ? matrix[0].length : matrix.length;
        }

        public int getNumberOfColumns() {
            return transpose ? matrix.length : matrix[0].length;
        }

        static class Coordinates {

            public final int row, col;

            public Coordinates(int row, int col) {
                this.row = row;
                this.col = col;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Coordinates that = (Coordinates) o;
                return row == that.row && col == that.col;
            }

            @Override
            public int hashCode() {
                return Objects.hash(row, col);
            }
        }
    }
}

@DefinitionCheck("checkClass")
class PartialMatchLengthUpdateValuesAsAutomatonTest {

    static Constructor<?> constructor;
    static Method getPartialMatchLengthUpdate;
    static Field theStates;
    static TransitionTest transitionTest;

    @BeforeAll
    public static void checkClass() {
        assumeTrue(definitionCorrect(FunctionToIntTest.class),
                "PartialMatchLengthUpdateValuesAsMatrix requires that interface FunctionToInt is implemented correctly");
        assumeTrue(definitionCorrect(PartialMatchLengthUpdateValuesTest.class),
                "PartialMatchLengthUpdateValuesAsMatrix requires that class PartialMatchLengthUpdateValues is implemented correctly");

        Class<?> partialMatchLengthUpdateValuesAsAutomatonClass = getClassForName("h03.PartialMatchLengthUpdateValuesAsAutomaton");

        transitionTest = new TransitionTest();

        // is generic
        TypeVariable<?>[] typeParameters = partialMatchLengthUpdateValuesAsAutomatonClass.getTypeParameters();

        assertEquals(1, typeParameters.length, "PartialMatchLengthUpdateValuesAsAutomaton must be generic");
        assertEquals("T", typeParameters[0].getName(), "Type parameter of class PartialMatchLengthUpdateValuesAsAutomaton is not named 'T'");

        // extends PartialMatchLengthUpdateValues<T>
        assertEquals(
                "h03.PartialMatchLengthUpdateValues<T>",
                partialMatchLengthUpdateValuesAsAutomatonClass.getGenericSuperclass().getTypeName(),
                "PartialMatchLengthUpdateValuesAsAutomaton must extend PartialMatchLengthUpdateValues"
        );

        // is not abstract
        assertFalse(
                isAbstract(partialMatchLengthUpdateValuesAsAutomatonClass.getModifiers()),
                "PartialMatchLengthUpdateValuesAsAutomaton must not be abstract"
        );

        // fields
        try {
            theStates = partialMatchLengthUpdateValuesAsAutomatonClass.getDeclaredField("theStates");
        } catch (NoSuchFieldException e) {
            fail("PartialMatchLengthUpdateValuesAsAutomaton is missing required field \"theStates\"", e);
        }

        assertTrue(isPrivate(theStates.getModifiers()), "Field theStates is not private");
        assertEquals("java.util.List<" + transitionTest.transitionClass.getName() + "<T>>[]", theStates.getGenericType().getTypeName(),
                "Field theStates does not have correct type");

        theStates.setAccessible(true);

        // constructors
        try {
            constructor = partialMatchLengthUpdateValuesAsAutomatonClass.getDeclaredConstructor(FunctionToIntTest.functionToIntIntf, Object[].class);
        } catch (NoSuchMethodException e) {
            fail("PartialMatchLengthUpdateValuesAsAutomaton is missing a required constructor", e);
        }

        // methods
        try {
            getPartialMatchLengthUpdate = partialMatchLengthUpdateValuesAsAutomatonClass.getDeclaredMethod("getPartialMatchLengthUpdate", int.class, Object.class);
        } catch (NoSuchMethodException e) {
            fail("PartialMatchLengthUpdateValuesAsAutomaton is missing required method \"getPartialMatchLengthUpdate(int, T)\"", e);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(RandomNeedleProvider.class)
    public void testTheStates(Character[] needle, int repeatLength) throws ReflectiveOperationException {
        Object functionToIntInstance = functionToIntProxyForAlphabet(alphabet);
        Object instance = constructor.newInstance(functionToIntInstance, needle);
        Object[] states = ((Object[]) theStates.get(instance));

        assertEquals(needle.length + 1, states.length, "theStates doesn't have correct length");

        for (int i = 0; i < needle.length; i++) {
            List<?> currentState = (List<?>) states[i];

            assertTrue(currentState.size() <= 2, "Each List in theStates has at most two elements");

            Object transition = currentState.get(0);

            assertEquals(i + 1, transitionTest.index.get(transition), "int-field of transition doesn't have the correct value");
            assertEquals(needle[i], ((List<?>) transitionTest.target.get(transition)).get(0), "List<T>-field of transition doesn't have the correct value");
        }

        assertEquals(repeatLength + 1, transitionTest.index.get(((List<?>) states[needle.length]).get(0)));
        assertEquals(needle[repeatLength], ((List<?>) transitionTest.target.get(((List<?>) states[needle.length]).get(0))).get(0));
    }

    @ParameterizedTest
    @ArgumentsSource(RandomNeedleProvider.class)
    public void testGetPartialMatchLengthUpdate(Character[] needle, @SuppressWarnings("unused") int repeatLength) throws ReflectiveOperationException {
        Object functionToIntInstance = functionToIntProxyForAlphabet(alphabet);
        Object instance = constructor.newInstance(functionToIntInstance, needle);

        // check that method returns correct values for match
        for (int i = 0; i < needle.length; i++)
            assertEquals(i + 1, getPartialMatchLengthUpdate.invoke(instance, i, needle[i]),
                    "getPartialMatchLengthUpdate(i, needle[i]) should return i + 1");

        // the method is rather trivial to implement, that the states array is correct is more important
        // TODO: this one is not _as_ trivial, maybe a more sophisticated test would be quite helpful?
        //  Fixed alphabet (A-Z) -> test that the returned value for Characters that are not part of the needle is 0
    }

    private static class TransitionTest {

        private Class<?> transitionClass;
        private Field index, target;

        public TransitionTest() {
            try {
                try {
                    transitionClass = Class.forName("h03.Transition");
                } catch (ClassNotFoundException e) {
                    transitionClass = Class.forName("h03.PartialMatchLengthUpdateValuesAsAutomaton$Transition");
                }

                // is generic
                TypeVariable<?>[] typeParameters = transitionClass.getTypeParameters();

                // you can comment out the following line if you want / have to
                assertEquals(1, typeParameters.length, "Transition is not generic (not strictly required but it makes more sense)");

                // fields
                for (Field field : transitionClass.getDeclaredFields())
                    if (isPublic(field.getModifiers()) && isFinal(field.getModifiers()))
                        if (field.getType().equals(int.class))
                            index = field;
                        else if (field.getType().equals(List.class))
                            target = field;

                assertTrue(index != null && target != null, "Transition is missing one or more required fields");

                index.setAccessible(true);
                target.setAccessible(true);
            } catch (ClassNotFoundException e) {
                assumeTrue(false, "Class " + e.getMessage() + " not found");
            }
        }
    }
}
