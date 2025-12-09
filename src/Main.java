import functions.*;
import functions.basic.Cos;
import functions.basic.Sin;
import java.io.*;

public class Main {
    private static final double EPSILON = 1e-10;

    public static void main(String[] args) {
        System.out.println("=== ТЕСТИРОВАНИЕ ВСЕХ ФУНКЦИОНАЛЬНОСТЕЙ ===\n");

        // Часть 1: Тестирование итераторов (for-each)
        testIterators();

        // Часть 2: Тестирование фабричного метода
        testFactories();

        // Часть 3: Тестирование рефлексии
        testReflection();

        System.out.println("\n=== ВСЕ ТЕСТЫ ЗАВЕРШЕНЫ ===");
    }

    private static boolean doublesEqual(double a, double b) {
        return Math.abs(a - b) < EPSILON;
    }

    private static void testIterators() {
        System.out.println("1. ТЕСТИРОВАНИЕ ИТЕРАТОРОВ (for-each):");
        System.out.println("======================================\n");

        // Создаем тестовые функции
        FunctionPoint[] points = {
                new FunctionPoint(0, 0),
                new FunctionPoint(1, 1),
                new FunctionPoint(2, 4),
                new FunctionPoint(3, 9),
                new FunctionPoint(4, 16)
        };

        // Тестируем ArrayTabulatedFunction
        System.out.println("a) ArrayTabulatedFunction:");
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(points);

        System.out.println("   Точки функции (через for-each):");
        int counter = 1;
        for (FunctionPoint p : arrayFunc) {
            System.out.printf("   %d. %s\n", counter++, p);
        }

        // Проверяем, что итератор возвращает правильные значения
        System.out.println("\n   Проверка значений через итератор:");
        counter = 0;
        boolean allPointsMatch = true;
        for (FunctionPoint p : arrayFunc) {
            FunctionPoint expected = points[counter];
            if (!doublesEqual(p.getX(), expected.getX()) || !doublesEqual(p.getY(), expected.getY())) {
                System.out.printf("   Ошибка: точка %d не совпадает\n", counter);
                allPointsMatch = false;
            }
            counter++;
        }
        if (allPointsMatch) {
            System.out.println("   Все точки совпадают с ожидаемыми");
        }

        // Тестируем LinkedListTabulatedFunction
        System.out.println("\nb) LinkedListTabulatedFunction:");
        TabulatedFunction listFunc = new LinkedListTabulatedFunction(points);

        System.out.println("   Точки функции (через for-each):");
        counter = 1;
        for (FunctionPoint p : listFunc) {
            System.out.printf("   %d. %s\n", counter++, p);
        }

        // Проверяем значения
        System.out.println("\n   Проверка значений через итератор:");
        counter = 0;
        allPointsMatch = true;
        for (FunctionPoint p : listFunc) {
            FunctionPoint expected = points[counter];
            if (!doublesEqual(p.getX(), expected.getX()) || !doublesEqual(p.getY(), expected.getY())) {
                System.out.printf("   Ошибка: точка %d не совпадает\n", counter);
                allPointsMatch = false;
            }
            counter++;
        }
        if (allPointsMatch) {
            System.out.println("   Все точки совпадают с ожидаемыми");
        }

        // Тестируем исключения итератора
        System.out.println("\nc) Тестирование исключений итератора:");
        try {
            var iterator = arrayFunc.iterator();
            while (iterator.hasNext()) {
                iterator.next();
            }
            // Попытка получить следующий элемент, когда их нет
            iterator.next();
        } catch (java.util.NoSuchElementException e) {
            System.out.println("   NoSuchElementException поймано: " + e.getMessage());
        }

        try {
            var iterator = listFunc.iterator();
            iterator.remove();
        } catch (UnsupportedOperationException e) {
            System.out.println("   UnsupportedOperationException поймано: " + e.getMessage());
        }
    }

    private static void testFactories() {
        System.out.println("\n\n2. ТЕСТИРОВАНИЕ ФАБРИЧНОГО МЕТОДА:");
        System.out.println("==================================\n");

        Function f = new Cos();
        TabulatedFunction tf;

        // Тестируем фабрику по умолчанию (ArrayTabulatedFunctionFactory)
        System.out.println("a) Фабрика по умолчанию:");
        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println("   Тип: " + tf.getClass().getSimpleName());
        System.out.println("   Количество точек: " + tf.getPointsCount());
        System.out.println("   Левая граница: " + tf.getLeftDomainBorder());
        System.out.println("   Правая граница: " + tf.getRightDomainBorder());

        // Проверяем значения косинуса
        System.out.println("\n   Проверка значений косинуса:");
        boolean cosValuesCorrect = true;
        for (int i = 0; i < tf.getPointsCount(); i++) {
            double x = tf.getPointX(i);
            double expectedY = Math.cos(x);
            double actualY = tf.getPointY(i);
            if (!doublesEqual(actualY, expectedY)) {
                System.out.printf("   Ошибка в точке %d: x=%.4f, ожидалось cos(x)=%.6f, получено %.6f\n", i, x, expectedY, actualY);
                cosValuesCorrect = false;
            }
        }
        if (cosValuesCorrect) {
            System.out.println("   Все значения косинуса вычислены корректно");
        }

        // Меняем фабрику на LinkedListTabulatedFunctionFactory
        System.out.println("\nb) Смена фабрики на LinkedListTabulatedFunctionFactory:");
        TabulatedFunctions.setTabulatedFunctionFactory(new LinkedListTabulatedFunction.LinkedListTabulatedFunctionFactory());
        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println("   Тип: " + tf.getClass().getSimpleName());
        System.out.println("   Количество точек: " + tf.getPointsCount());

        // Возвращаем фабрику по умолчанию
        System.out.println("\nc) Возвращаем ArrayTabulatedFunctionFactory:");
        TabulatedFunctions.setTabulatedFunctionFactory(new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory());
        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println("   Тип: " + tf.getClass().getSimpleName());

        // Тестируем методы createTabulatedFunction
        System.out.println("\nd) Методы createTabulatedFunction:");

        // Метод 1: createTabulatedFunction(double, double, int)
        System.out.println("   Метод 1 (границы + количество точек):");
        TabulatedFunction tf1 = TabulatedFunctions.createTabulatedFunction(0, 10, 5);
        System.out.println("   Тип: " + tf1.getClass().getSimpleName() + ", точек: " + tf1.getPointsCount());

        // Проверяем равномерность распределения точек
        System.out.println("   Проверка равномерности точек:");
        boolean uniformDistribution = true;
        double step = 2.5; // (10 - 0) / (5 - 1) = 2.5
        for (int i = 0; i < tf1.getPointsCount(); i++) {
            double expectedX = i * step;
            double actualX = tf1.getPointX(i);
            if (!doublesEqual(actualX, expectedX)) {
                System.out.printf("   Точка %d: ожидалось x=%.1f, получено x=%.1f\n", i, expectedX, actualX);
                uniformDistribution = false;
            }
        }
        if (uniformDistribution) {
            System.out.println("   Точки распределены равномерно");
        }

        // Метод 2: createTabulatedFunction(double, double, double[])
        System.out.println("\n   Метод 2 (границы + значения):");
        double[] values = {0, 1, 4, 9, 16};
        TabulatedFunction tf2 = TabulatedFunctions.createTabulatedFunction(0, 4, values);
        System.out.println("   Тип: " + tf2.getClass().getSimpleName() + ", точек: " + tf2.getPointsCount());

        // Проверяем значения
        System.out.println("   Проверка значений:");
        boolean valuesMatch = true;
        for (int i = 0; i < tf2.getPointsCount(); i++) {
            if (!doublesEqual(tf2.getPointY(i), values[i])) {
                System.out.printf("   ✗ Точка %d: ожидалось y=%.1f, получено y=%.1f\n", i, values[i], tf2.getPointY(i));
                valuesMatch = false;
            }
        }
        if (valuesMatch) {
            System.out.println("   Все значения сохранены корректно");
        }

        // Метод 3: createTabulatedFunction(FunctionPoint[])
        System.out.println("\n   Метод 3 (массив точек):");
        FunctionPoint[] points = {new FunctionPoint(0, 0), new FunctionPoint(1, 1), new FunctionPoint(2, 4)};
        TabulatedFunction tf3 = TabulatedFunctions.createTabulatedFunction(points);
        System.out.println("   Тип: " + tf3.getClass().getSimpleName() + ", точек: " + tf3.getPointsCount());

        // Проверяем точки
        System.out.println("   Проверка точек:");
        boolean pointsMatch = true;
        for (int i = 0; i < tf3.getPointsCount(); i++) {
            FunctionPoint expected = points[i];
            if (!doublesEqual(tf3.getPointX(i), expected.getX()) || !doublesEqual(tf3.getPointY(i), expected.getY())) {
                System.out.printf("   Точка %d не совпадает\n", i);
                pointsMatch = false;
            }
        }
        if (pointsMatch) {
            System.out.println("   Все точки сохранены корректно");
        }
    }

    private static void testReflection() {
        System.out.println("\n\n3. ТЕСТИРОВАНИЕ РЕФЛЕКСИИ:");
        System.out.println("===========================\n");

        TabulatedFunction f;

        // 1. Создание через рефлексию с конструктором (double, double, int)
        System.out.println("a) Создание ArrayTabulatedFunction (границы + количество точек):");
        try {
            f = TabulatedFunctions.createTabulatedFunction(ArrayTabulatedFunction.class, 0, 10, 3);
            System.out.println("   Тип: " + f.getClass().getSimpleName());
            System.out.println("   Данные: " + f);

            // Проверяем точки
            System.out.println("   Проверка точек:");
            boolean pointsCorrect = true;
            double[] expectedXs = {0.0, 5.0, 10.0}; // (10-0)/(3-1)=5
            for (int i = 0; i < f.getPointsCount(); i++) {
                if (!doublesEqual(f.getPointX(i), expectedXs[i])) {
                    System.out.printf("   ✗ Точка %d: ожидалось x=%.1f, получено x=%.1f\n", i, expectedXs[i], f.getPointX(i));
                    pointsCorrect = false;
                }
            }
            if (pointsCorrect) {
                System.out.println("   Точки созданы корректно");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("   Ошибка: " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("   Причина: " + e.getCause().getMessage());
            }
        }

        // 2. Создание через рефлексию с конструктором (double, double, double[])
        System.out.println("\nb) Создание ArrayTabulatedFunction (границы + значения):");
        try {
            double[] testValues = {0, 10, 20};
            f = TabulatedFunctions.createTabulatedFunction(ArrayTabulatedFunction.class, 0, 10, testValues);
            System.out.println("   Тип: " + f.getClass().getSimpleName());
            System.out.println("   Данные: " + f);

            // Проверяем значения
            System.out.println("   Проверка значений:");
            boolean valuesCorrect = true;
            for (int i = 0; i < f.getPointsCount(); i++) {
                if (!doublesEqual(f.getPointY(i), testValues[i])) {
                    System.out.printf("   ✗ Точка %d: ожидалось y=%.1f, получено y=%.1f\n",
                            i, testValues[i], f.getPointY(i));
                    valuesCorrect = false;
                }
            }
            if (valuesCorrect) {
                System.out.println("   Значения сохранены корректно");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("   Ошибка: " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("   Причина: " + e.getCause().getMessage());
            }
        }

        // 3. Создание LinkedListTabulatedFunction через рефлексию
        System.out.println("\nc) Создание LinkedListTabulatedFunction (массив точек):");
        try {
            FunctionPoint[] testPoints = {new FunctionPoint(0, 0), new FunctionPoint(10, 10)};
            f = TabulatedFunctions.createTabulatedFunction(LinkedListTabulatedFunction.class, testPoints);
            System.out.println("   Тип: " + f.getClass().getSimpleName());
            System.out.println("   Данные: " + f);

            // Проверяем точки
            System.out.println("   Проверка точек:");
            boolean pointsMatch = true;
            for (int i = 0; i < f.getPointsCount(); i++) {
                if (!doublesEqual(f.getPointX(i), testPoints[i].getX()) ||
                        !doublesEqual(f.getPointY(i), testPoints[i].getY())) {
                    System.out.printf("   ✗ Точка %d не совпадает\n", i);
                    pointsMatch = false;
                }
            }
            if (pointsMatch) {
                System.out.println("   Точки сохранены корректно");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("   Ошибка: " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("   Причина: " + e.getCause().getMessage());
            }
        }

        // 4. Табулирование функции Sin с указанием класса через рефлексию
        System.out.println("\nd) Табулирование функции Sin через рефлексию:");
        try {
            f = TabulatedFunctions.tabulate(LinkedListTabulatedFunction.class, new Sin(), 0, Math.PI, 11);
            System.out.println("   Тип: " + f.getClass().getSimpleName());
            System.out.println("   Количество точек: " + f.getPointsCount());

            // Проверяем значения синуса
            System.out.println("   Проверка значений синуса (первые 3 точки):");
            boolean sinValuesCorrect = true;
            int checkCount = Math.min(3, f.getPointsCount());
            for (int i = 0; i < checkCount; i++) {
                double x = f.getPointX(i);
                double expectedSin = Math.sin(x);
                double actualSin = f.getPointY(i);

                if (!doublesEqual(actualSin, expectedSin)) {
                    System.out.printf("   Точка %d: x=%.4f, ожидалось sin(x)=%.6f, получено %.6f\n", i, x, expectedSin, actualSin);
                    sinValuesCorrect = false;
                } else {
                    System.out.printf("   Точка %d: x=%.4f, sin(x)=%.6f\n", i, x, actualSin);
                }
            }

            if (sinValuesCorrect && checkCount > 0) {
                System.out.println("   Первые " + checkCount + " точек вычислены корректно");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("   Ошибка: " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("   Причина: " + e.getCause().getMessage());
            }
        }

        // 5. Тестирование обработки ошибок рефлексии
        System.out.println("\ne) Тестирование обработки ошибок рефлексии:");

        // 5.1. Класс не реализует TabulatedFunction
        System.out.println("\n   e.1) Класс не реализует TabulatedFunction:");
        try {
            f = TabulatedFunctions.createTabulatedFunction(Integer.class, 0, 10, 3);
            System.out.println("   ОШИБКА: исключение не было выброшено!");
        } catch (IllegalArgumentException e) {
            System.out.println("   " + e.getMessage());
        }

        // 5.2. Неправильные аргументы (ошибка в конструкторе)
        System.out.println("\n   e.2) Ошибка в конструкторе (leftX >= rightX):");
        try {
            f = TabulatedFunctions.createTabulatedFunction(ArrayTabulatedFunction.class, 10, 0, 3); // leftX > rightX
            System.out.println("   Объект создан, хотя не должен был");
        } catch (IllegalArgumentException e) {
            System.out.println("   " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("   Исключение в конструкторе: " + e.getCause().getMessage());
            }
        }

        // 6. Сравнение фабрики и рефлексии
        System.out.println("\nf) Сравнение фабрики и рефлексии:");

        // Через фабрику (текущая - ArrayTabulatedFunctionFactory)
        System.out.println("   Через фабрику (текущая):");
        TabulatedFunction factoryFunc = TabulatedFunctions.createTabulatedFunction(0, 5, 3);
        System.out.println("   Тип: " + factoryFunc.getClass().getSimpleName());

        // Через рефлексию (явно указываем LinkedListTabulatedFunction)
        System.out.println("\n   Через рефлексию (LinkedListTabulatedFunction):");
        TabulatedFunction reflectFunc = TabulatedFunctions.createTabulatedFunction(LinkedListTabulatedFunction.class, 0, 5, 3);
        System.out.println("   Тип: " + reflectFunc.getClass().getSimpleName());

        // Проверяем, что оба метода создают функции с одинаковыми X
        System.out.println("\n   Сравнение координат X:");
        boolean xCoordinatesMatch = true;
        if (factoryFunc.getPointsCount() == reflectFunc.getPointsCount()) {
            for (int i = 0; i < factoryFunc.getPointsCount(); i++) {
                double factoryX = factoryFunc.getPointX(i);
                double reflectX = reflectFunc.getPointX(i);
                if (!doublesEqual(factoryX, reflectX)) {
                    System.out.printf("  Точка %d: фабрика x=%.2f, рефлексия x=%.2f\n", i, factoryX, reflectX);
                    xCoordinatesMatch = false;
                }
            }
            if (xCoordinatesMatch) {
                System.out.println("   Координаты X совпадают");
            }
        } else {
            System.out.println("   Разное количество точек");
        }

        // 7. Тестирование сериализации с рефлексией
        System.out.println("\ng) Тестирование сериализации с рефлексией:");
        try {
            // Создаем функцию через рефлексию
            TabulatedFunction original = TabulatedFunctions.createTabulatedFunction(
                    LinkedListTabulatedFunction.class,
                    new FunctionPoint[] {new FunctionPoint(0, 0),
                            new FunctionPoint(Math.PI/2, 1),
                            new FunctionPoint(Math.PI, 0)
                    }
            );

            System.out.println("   Исходная функция: " + original.getClass().getSimpleName());

            // Сериализуем
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            TabulatedFunctions.outputTabulatedFunction(original, byteOut);

            // Десериализуем с указанием другого класса через рефлексию
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            TabulatedFunction restored = TabulatedFunctions.inputTabulatedFunction(ArrayTabulatedFunction.class, byteIn);

            System.out.println("   Восстановленная функция: " + restored.getClass().getSimpleName());

            // Проверяем совпадение данных с учетом погрешности
            boolean sameData = true;
            for (int i = 0; i < original.getPointsCount(); i++) {
                double originalX = original.getPointX(i);
                double originalY = original.getPointY(i);
                double restoredX = restored.getPointX(i);
                double restoredY = restored.getPointY(i);

                if (!doublesEqual(originalX, restoredX) || !doublesEqual(originalY, restoredY)) {
                    System.out.printf("   Точка %d не совпадает: " + "оригинал (%.6f, %.6f), восстановлено (%.6f, %.6f)\n",
                            i, originalX, originalY, restoredX, restoredY);
                    sameData = false;
                }
            }
            if (sameData) {
                System.out.println("   Все точки совпадают с точностью " + EPSILON);
            }

        } catch (Exception e) {
            System.err.println("   Ошибка: " + e.getMessage());
            e.printStackTrace();
        }

        // 8. Комплексный тест
        System.out.println("\nh) Комплексный тест всех механизмов:");

        // Создаем функцию косинуса
        Function cosFunc = new Cos();

        // Табулируем через рефлексию
        TabulatedFunction tabulatedCos = TabulatedFunctions.tabulate(LinkedListTabulatedFunction.class, cosFunc, 0, 2*Math.PI, 9);

        System.out.println("   Создана функция: " + tabulatedCos.getClass().getSimpleName());
        System.out.println("   Точек: " + tabulatedCos.getPointsCount());

        // Проверяем значения с учетом погрешности
        System.out.println("\n   Проверка значений косинуса:");
        int correctValues = 0;
        int totalValues = tabulatedCos.getPointsCount();

        for (int i = 0; i < totalValues; i++) {
            double x = tabulatedCos.getPointX(i);
            double expectedCos = Math.cos(x);
            double actualCos = tabulatedCos.getPointY(i);

            if (doublesEqual(actualCos, expectedCos)) {
                correctValues++;
            } else {
                System.out.printf("  Точка %d: x=%.4f, ожидалось cos=%.6f, получено %.6f\n",
                        i, x, expectedCos, actualCos);
            }
        }

        System.out.printf("   Корректно вычислено: %d/%d точек\n", correctValues, totalValues);

        // Используем for-each для вывода
        System.out.println("\n   Вывод через for-each:");
        int count = 0;
        for (FunctionPoint p : tabulatedCos) {
            System.out.printf("   x = %6.4f, cos(x) = %8.6f\n", p.getX(), p.getY());
            count++;
        }

        // Проверка границ
        System.out.println("\n   Проверка границ области определения:");
        double leftBorder = tabulatedCos.getLeftDomainBorder();
        double rightBorder = tabulatedCos.getRightDomainBorder();

        if (doublesEqual(leftBorder, 0.0) && doublesEqual(rightBorder, 2*Math.PI)) {
            System.out.println("   Границы корректны: [0, 2π]");
        } else {
            System.out.printf("   Ошибка границ: ожидалось [0, %.4f], получено [%.4f, %.4f]\n",
                    2*Math.PI, leftBorder, rightBorder);
        }
    }
}