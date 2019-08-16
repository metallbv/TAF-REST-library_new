package ua.com.epam.data;

import com.github.javafaker.Faker;
import ua.com.epam.config.DataProp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class DataIngestion {
    private static DataProp prop = new DataProp();
    private static String fileName = "addData";

    private static final String author = "insert into author(authorId, `authorName.first`, `authorName.second`, `birth.city`, `birth.country`, `birth.date`, authorDescription, nationality) values(%d, \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\");";
    private static final String genre = "insert into genre(genreId, genreName, genreDescription) values(\"%s\", \"%s\", \"%s\");";
    private static final String book = "insert into book(bookId, bookName, bookLanguage, bookDescription, `additional.size.height`, `additional.size.length`, `additional.size.width`, `additional.pageCount`, publicationYear) values(%d, \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", %d, %d);";

    private final static int authorsCount = 90; // 9999 - is maximum (but can generate too much time)
    private final static int genresCount = 30;  // 30 is maximum; if set more, will work endlessly!!!
    private final static int booksCount = 270;  // 9999 - is maximum (but can generate too much time)

    public static void main(String[] args) throws ParseException {
        Faker f = new Faker();
        List<String> sqlInsert = new ArrayList<>();
        sqlInsert.add("use " + prop.dbName() + ";");

        //Author
        //generate unique Author ids;
        List<Long> authorIds = new ArrayList<>();
        while (authorIds.size() < authorsCount) {
            long id = f.number().numberBetween(1L, 9999L);
            if (!authorIds.contains(id)) authorIds.add(id);
        }

        String[] nationalities = {"Albanian", "American", "Australian", "Austrian", "Belgian", "British", "Bulgarian",
                "Canadian", "Chinese", "Czech", "Dutch", "Egyptian", "French", "German", "Greek", "Indian", "Irish",
                "Lithuanian", "Malaysian", "Mexican", "Moldovan", "New Zealander", "Romanian", "Scottish", "Spanish",
                "Swedish", "Turkish", "Ukrainian", "Welsh", "Syrian", "Slovenian", "Slovakian", "Polish", "Peruvian",
                "Namibian", "Nepalese", "Afghan", "Andorran", "Angolan", "Armenian", "Bahamian", "Cambodian",
                "Central African", "Colombian", "Cuban", "Equatorial Guinean", "Icelander", "Indonesian",
                "Kittian and Nevisian", "Liechtensteiner", "Lithuanian", "Luxembourger", "Maldivan", "Mongolian"};

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date from = formatter.parse("1920-01-01");
        Date to = formatter.parse("1999-12-31");

        authorIds.stream()
                .map(id -> createAuthor(
                        id,
                        f.name().firstName(), f.name().lastName(),
                        nationalities[new Random().nextInt(nationalities.length)],
                        formatter.format(f.date().between(from, to)), f.address().country(), f.address().city(),
                        f.lorem().paragraph()))
                .forEach(sqlInsert::add);

        //genre
        //generate all possible genre names
        List<String> genreNames = new ArrayList<>();
        while (genreNames.size() < genresCount) {
            String name = f.book().genre();
            if (!genreNames.contains(name)) genreNames.add(name);
        }

        //generate unique genre ids
        List<Long> genreIds = new ArrayList<>();
        while (genreIds.size() < genreNames.size()) {
            long id = f.number().numberBetween(1L, 9999L);
            if (!genreIds.contains(id)) genreIds.add(id);
        }

        IntStream.range(0, genreIds.size())
                .mapToObj(i -> createGenre(genreIds.get(i), genreNames.get(i), f.lorem().paragraph()))
                .forEach(sqlInsert::add);

        //book
        List<Long> bookIds = new ArrayList<>();
        while (bookIds.size() < booksCount) {
            long id = f.number().numberBetween(1L, 9999L);
            if (!bookIds.contains(id)) bookIds.add(id);
        }

        String[] languages = {"ukrainian", "german", "russian", "polish", "spanish", "belorussian", "chinese", "english"};

        bookIds.stream()
                .map(bookId -> createBook(bookId,
                        f.book().title(),
                        languages[new Random().nextInt(languages.length)],
                        f.lorem().paragraph(),
                        f.number().numberBetween(10, 1000),
                        f.number().randomDouble(1, 5, 40),
                        f.number().randomDouble(1, 1, 5),
                        f.number().randomDouble(1, 5, 40),
                        f.number().numberBetween(1970, 2019)))
                .forEach(sqlInsert::add);

        File script = new File("src/test/resources/" + fileName + ".sql");
        try {
            script.createNewFile();
            Files.write(script.toPath(), sqlInsert);
            System.out.println("Dump generated successfully with name: \'" + fileName + "\'.\nSee in \'src/test/resources\'!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String createAuthor(long authorId, String first, String second, String nationality,
                                       String birthDate, String country, String city, String descr) {
        return String.format(author, authorId, first, second, city, country, birthDate, descr, nationality);
    }

    private static String createGenre(long genreId, String genreName, String genreDescription) {
        return String.format(genre, genreId, genreName, genreDescription);
    }

    private static String createBook(long bookId, String bookName, String bookLang, String bookDescr, int pageCount,
                                     double height, double width, double length, int pubYear) {
        return String.format(book, bookId, bookName, bookLang, bookDescr, height, length, width, pageCount, pubYear);
    }
}
