package functions;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.StringTokenizer;

public class TabulatedFunctions {
    // Статическое поле для хранения текущей фабрики
    private static TabulatedFunctionFactory factory = new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory();

    // Метод для установки фабрики
    public static void setTabulatedFunctionFactory(TabulatedFunctionFactory factory) {
        TabulatedFunctions.factory = factory;
    }

    private TabulatedFunctions() {
        throw new AssertionError("Невозможно создать экземпляр класса TabulatedFunctions");
    }

    // === МЕТОДЫ С ИСПОЛЬЗОВАНИЕМ ФАБРИКИ ===

    // Три перегруженных метода createTabulatedFunction (фабричные)
    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
        return factory.createTabulatedFunction(leftX, rightX, pointsCount);
    }

    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
        return factory.createTabulatedFunction(leftX, rightX, values);
    }

    public static TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
        return factory.createTabulatedFunction(points);
    }

    // === МЕТОДЫ С ИСПОЛЬЗОВАНИЕМ РЕФЛЕКСИИ ===

    public static TabulatedFunction createTabulatedFunction(
            Class<?> clazz, double leftX, double rightX, int pointsCount) {

        // Проверяем, что класс реализует TabulatedFunction
        if (!TabulatedFunction.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(
                    "Класс " + clazz.getName() + " не реализует интерфейс TabulatedFunction");
        }

        try {
            // Находим конструктор с параметрами (double, double, int)
            Constructor<?> constructor = clazz.getConstructor(double.class, double.class, int.class);

            // Создаем объект с помощью конструктора
            return (TabulatedFunction) constructor.newInstance(leftX, rightX, pointsCount);

        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    "Класс " + clazz.getName() + " не имеет конструктора (double, double, int)", e);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Ошибка при создании объекта класса " + clazz.getName(), e);
        }
    }

    public static TabulatedFunction createTabulatedFunction(
            Class<?> clazz, double leftX, double rightX, double[] values) {

        // Проверяем, что класс реализует TabulatedFunction
        if (!TabulatedFunction.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(
                    "Класс " + clazz.getName() + " не реализует интерфейс TabulatedFunction");
        }

        try {
            // Находим конструктор с параметрами (double, double, double[])
            Constructor<?> constructor = clazz.getConstructor(double.class, double.class, double[].class);

            // Создаем объект с помощью конструктора
            return (TabulatedFunction) constructor.newInstance(leftX, rightX, values);

        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    "Класс " + clazz.getName() + " не имеет конструктора (double, double, double[])", e);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Ошибка при создании объекта класса " + clazz.getName(), e);
        }
    }

    public static TabulatedFunction createTabulatedFunction(
            Class<?> clazz, FunctionPoint[] points) {

        // Проверяем, что класс реализует TabulatedFunction
        if (!TabulatedFunction.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(
                    "Класс " + clazz.getName() + " не реализует интерфейс TabulatedFunction");
        }

        try {
            // Находим конструктор с параметрами (FunctionPoint[])
            Constructor<?> constructor = clazz.getConstructor(FunctionPoint[].class);

            // Создаем объект с помощью конструктора
            return (TabulatedFunction) constructor.newInstance((Object) points);

        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    "Класс " + clazz.getName() + " не имеет конструктора (FunctionPoint[])", e);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Ошибка при создании объекта класса " + clazz.getName(), e);
        }
    }

    // === ПЕРЕГРУЖЕННЫЕ ВЕРСИИ tabulate С РЕФЛЕКСИЕЙ ===

    public static TabulatedFunction tabulate(
            Class<?> clazz, Function function, double leftX, double rightX, int pointsCount) {

        // Проверка корректности параметров
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2: " + pointsCount);
        }
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой: " + leftX + " >= " + rightX);
        }

        // Проверка области определения
        double functionLeftBorder = function.getLeftDomainBorder();
        double functionRightBorder = function.getRightDomainBorder();
        if (leftX < functionLeftBorder || rightX > functionRightBorder) {
            throw new IllegalArgumentException("Границы табулирования [" + leftX + ", " + rightX + "] " +
                    "выходят за область определения функции [" + functionLeftBorder + ", " + functionRightBorder + "]"
            );
        }

        // Создание массива значений Y путем вычисления функции в равномерно распределенных точках
        double[] values = new double[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            values[i] = function.getFunctionValue(x);
        }

        // Используем рефлексию для создания объекта
        return createTabulatedFunction(clazz, leftX, rightX, values);
    }

    // === СУЩЕСТВУЮЩИЕ МЕТОДЫ (с использованием фабрики) ===

    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        // Проверка корректности параметров
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2: " + pointsCount);
        }
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой: " + leftX + " >= " + rightX);
        }

        // Проверка области определения
        double functionLeftBorder = function.getLeftDomainBorder();
        double functionRightBorder = function.getRightDomainBorder();
        if (leftX < functionLeftBorder || rightX > functionRightBorder) {
            throw new IllegalArgumentException("Границы табулирования [" + leftX + ", " + rightX + "] " +
                    "выходят за область определения функции [" + functionLeftBorder + ", " + functionRightBorder + "]"
            );
        }

        // Создание массива значений Y путем вычисления функции в равномерно распределенных точках
        double[] values = new double[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            values[i] = function.getFunctionValue(x);
        }
        // Используем фабрику вместо прямого создания
        return createTabulatedFunction(leftX, rightX, values);
    }

    // === СУЩЕСТВУЮЩИЕ МЕТОДЫ СЕРИАЛИЗАЦИИ ===

    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) {
        DataOutputStream dataOut = new DataOutputStream(out);
        try {
            int pointsCount = function.getPointsCount();
            dataOut.writeInt(pointsCount);
            for (int i = 0; i < pointsCount; i++) {
                dataOut.writeDouble(function.getPointX(i));
                dataOut.writeDouble(function.getPointY(i));
            }
            dataOut.flush();
        }
        catch (IOException e) {
            throw new RuntimeException("Ошибка при выводе табулированной функции", e);
        }
    }

    public static TabulatedFunction inputTabulatedFunction(InputStream in) {
        DataInputStream dataIn = new DataInputStream(in);
        try {
            int pointsCount = dataIn.readInt();
            if (pointsCount < 2) {
                throw new RuntimeException("Некорректное количество точек: " + pointsCount);
            }
            FunctionPoint[] points = new FunctionPoint[pointsCount];
            for (int i = 0; i < pointsCount; i++) {
                double x = dataIn.readDouble();
                double y = dataIn.readDouble();
                points[i] = new FunctionPoint(x, y);
            }
            // Используем фабрику вместо прямого создания
            return createTabulatedFunction(points);
        }
        catch (IOException e) {
            throw new RuntimeException("Ошибка при вводе табулированной функции", e);
        }
    }

    // === ДОПОЛНИТЕЛЬНЫЙ МЕТОД inputTabulatedFunction С РЕФЛЕКСИЕЙ ===

    public static TabulatedFunction inputTabulatedFunction(Class<?> clazz, InputStream in) {
        DataInputStream dataIn = new DataInputStream(in);
        try {
            int pointsCount = dataIn.readInt();
            if (pointsCount < 2) {
                throw new RuntimeException("Некорректное количество точек: " + pointsCount);
            }
            FunctionPoint[] points = new FunctionPoint[pointsCount];
            for (int i = 0; i < pointsCount; i++) {
                double x = dataIn.readDouble();
                double y = dataIn.readDouble();
                points[i] = new FunctionPoint(x, y);
            }
            // Используем рефлексию для создания объекта
            return createTabulatedFunction(clazz, points);
        }
        catch (IOException e) {
            throw new RuntimeException("Ошибка при вводе табулированной функции", e);
        }
    }

    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) {
        PrintWriter writer = new PrintWriter(new BufferedWriter(out));
        try {
            int pointsCount = function.getPointsCount();
            writer.print(pointsCount);

            for (int i = 0; i < pointsCount; i++) {
                writer.print(" " + function.getPointX(i));
                writer.print(" " + function.getPointY(i));
            }
            writer.flush();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при записи табулированной функции", e);
        }
    }

    public static TabulatedFunction readTabulatedFunction(Reader in) {
        // Используем StringTokenizer вместо StreamTokenizer для упрощения
        try (BufferedReader reader = new BufferedReader(in)) {
            String line = reader.readLine();
            if (line == null) {
                throw new RuntimeException("Пустой ввод");
            }

            StringTokenizer tokenizer = new StringTokenizer(line);
            if (!tokenizer.hasMoreTokens()) {
                throw new RuntimeException("Ожидалось количество точек");
            }

            int pointsCount = Integer.parseInt(tokenizer.nextToken());
            if (pointsCount < 2) {
                throw new RuntimeException("Некорректное количество точек: " + pointsCount);
            }

            FunctionPoint[] points = new FunctionPoint[pointsCount];
            for (int i = 0; i < pointsCount; i++) {
                if (!tokenizer.hasMoreTokens()) {
                    throw new RuntimeException("Ожидалась координата X точки " + i);
                }
                double x = Double.parseDouble(tokenizer.nextToken());

                if (!tokenizer.hasMoreTokens()) {
                    throw new RuntimeException("Ожидалась координата Y точки " + i);
                }
                double y = Double.parseDouble(tokenizer.nextToken());

                points[i] = new FunctionPoint(x, y);
            }
            // Используем фабрику вместо прямого создания
            return createTabulatedFunction(points);
        }
        catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении табулированной функции", e);
        }
        catch (NumberFormatException e) {
            throw new RuntimeException("Некорректный формат числа", e);
        }
    }

    // === ДОПОЛНИТЕЛЬНЫЙ МЕТОД readTabulatedFunction С РЕФЛЕКСИЕЙ ===

    public static TabulatedFunction readTabulatedFunction(Class<?> clazz, Reader in) {
        // Используем StringTokenizer вместо StreamTokenizer для упрощения
        try (BufferedReader reader = new BufferedReader(in)) {
            String line = reader.readLine();
            if (line == null) {
                throw new RuntimeException("Пустой ввод");
            }

            StringTokenizer tokenizer = new StringTokenizer(line);
            if (!tokenizer.hasMoreTokens()) {
                throw new RuntimeException("Ожидалось количество точек");
            }

            int pointsCount = Integer.parseInt(tokenizer.nextToken());
            if (pointsCount < 2) {
                throw new RuntimeException("Некорректное количество точек: " + pointsCount);
            }

            FunctionPoint[] points = new FunctionPoint[pointsCount];
            for (int i = 0; i < pointsCount; i++) {
                if (!tokenizer.hasMoreTokens()) {
                    throw new RuntimeException("Ожидалась координата X точки " + i);
                }
                double x = Double.parseDouble(tokenizer.nextToken());

                if (!tokenizer.hasMoreTokens()) {
                    throw new RuntimeException("Ожидалась координата Y точки " + i);
                }
                double y = Double.parseDouble(tokenizer.nextToken());

                points[i] = new FunctionPoint(x, y);
            }
            // Используем рефлексию для создания объекта
            return createTabulatedFunction(clazz, points);
        }
        catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении табулированной функции", e);
        }
        catch (NumberFormatException e) {
            throw new RuntimeException("Некорректный формат числа", e);
        }
    }
}