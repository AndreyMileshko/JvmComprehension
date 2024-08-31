/**
 * Начинает работать система загрузки классов (ClassLoader):
 * Анализ кода выявляет классы которые необходимо загрузить: JvmComprehension, Object, Integer, System, а так же
 * "подкапотные" классы:
 * - JvmComprehension: Application ClassLoader принимает класс и сразу передаёт его Platform ClassLoader'у,
 *                     а он в свою очередь сразу передаёт его Bootstrap ClassLoader'у. Bootstrap ClassLoader отвечает
 *                     за библиотечные пакеты классов за исключением пакета java.util.*, соответственно искомый класс в
 *                     этих пакетах отсутствует, поэтому Bootstrap ClassLoader возвращает его Platform ClassLoader'у.
 *                     Platform ClassLoader отвечает за классы находящиеся в пакете java.util.*, поэтому он так же не находит
 *                     его и передаёт назад Application ClassLoader'у, который уже загружает наш класс. Причем перед
 *                     началом поиска каждый ClassLoader проверяет свой кэш, в случае если класс уже был загружен и
 *                     помещен в кэш, то ClassLoader просто его вытащит оттуда.
 * - Object, Integer, System: Находятся в пакете java.lang.* поэтому они будут загружены Bootstrap ClassLoader'ом так
 *                            же проходя выше упомянутую цепочку загрузчиков.
 *                            (Application ClassLoader -> Platform ClassLoader -> Bootstrap ClassLoader)
 *
 * После загрузки каждого класса наступает этап связывания (Linking):
 * - Verify: проверяется синтаксис и полнота загрузки класса;
 * - Prepare: происходит подготовка примитивов в статических полях;
 * - Resolve: выявляются связанные классы и так же отправляются на загрузку.
 *
 * После каждый класс проходит этап инициализации(initialization): выполняются статические инициализаторы и статические методы.
 *
 * Затем все загруженные классы отправляются в Metaspace (специальная область памяти).
 */

public class JvmComprehension {
    /**
     * Начинает работать метод main -> в stack (Stack memory - динамический участок памяти, который работает по принципу
     * «последним пришёл, первым вышел» (last in first out, LIFO)) отражается фрейм метода.
     */
    public static void main(String[] args) {
        /**
         * Во фрейме метода main в стеке выделяется память, которая будет хранить
         * переменную i примитивного типа int и её значение 1.
         */
        int i = 1;                      // 1
        /**
         * Отрабатывает оператор new -> выделяется память для хранения объекта в heap(куче) ->
         * отрабатывает конструктор Object() класса Object -> в фрейме метода mein отражается переменная o и ссылка на
         * созданный объект в куче.
         */
        Object o = new Object();        // 2
        /**
         * Аналогично предыдущей строке кода: new -> конструктор с параметром 2 -> в фрейме main переменная хранящая ссылку на
         * объект ii, а сам объект хранится в куче.
         */
        Integer ii = 2;                 // 3
        /**
         * В stack открывается новый фрейм метода printAll. Метод принимает 3 параметра (Object o, int i, Integer ii)
         * значение примитивного параметра i будет передано в фрейм метода printAll по значению(1), а ссылочные параметры
         * (Object o, Integer ii) будут также отражены в фрейме и содержать ссылки на ранее созданные объекты в куче
         * (то есть на объекты о и ii будут уже ссылаться разные переменные(даже если название у них совпадает) из двух методов).
         */
        printAll(o, i, ii);             // 4
        /**
         * Будет создана новая переменная класса String в фрейме класса main, которая будет содержать ссылку на переменную
         * в куче со значением "finished"(если опустить "подкапотную" часть создания строк -> массив char и тд),
         * которая будет передана в метод println переменной out(PrintStream -> FilterOutputStream -> OutputStream) класса System.
         * Откроется новый фрейм в стеке метода println -> в стеке будет отражена переменная, которая ссылается на ранее
         * созданный объект класса String("finished") в куче. По завершению работы метода println, в консоль будет выведена
         * строка "finished", после чего в стеке будет смещён указатель фрейма назад на метод main.
         */
        System.out.println("finished"); // 7
/**
 *  Process finished with exit code 0.
 */
    }

    private static void printAll(Object o, int i, Integer ii) {
        /**
         * Все полностью аналогично созданию объекта ii за исключением самого фрейма в стеке
         * (переменная uselessVar отражается в фрейме метода printAll и хранит ссылку на сам объект с типом Integer в куче)
         */
        Integer uselessVar = 700;                   // 5
        /**
         * К переменной out класса System будет применён метод println с параметром -> соответственно будет открыт новый
         * фрейм метода println который будет содержать переменную-параметр (o.toString() + i + ii), которая будет ссылаться
         * на новый объект класса String в куче. А так как в этой переменной вызывается еще один метод toString() у объекта
         * о, будет открыт еще один фрейм этого метода в стеке, по завершению работы которого он вернёт строковое представление
         * объекта о в переменную метода println. Произойдёт конкатенация строк и метод println сможет завершить свою работу
         * отразив в консоле полученную строку.
         */
        System.out.println(o.toString() + i + ii);  // 6
        /**
         * Метод printAll завершит свою работу, и в стеке будет смещён указатель фрейма назад на метод main.
         * (Формально из стека будет удалён метод printAll как завершенный)
         */
    }
}