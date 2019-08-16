package ua.com.epam.crud;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import ua.com.epam.BaseTest;
import ua.com.epam.entity.author.Author;
import ua.com.epam.entity.author.nested.Birth;

import java.time.LocalDate;
import java.util.List;

import static ua.com.epam.config.URI.*;

// yes, that looks pretty simple... but how to resolve
// problem if it will be needed to make get call
// with query parameters..? (like ?key1=value1&key2=value2)
// and then check if our response is sorted by
// some parameter... or is paginated or something else?
// try to solve it on service layer

// here are two HTTP operations (POST and GET)
// and pretty simple tests that check if them work correctly
@Test(description = "CRUD operations for author table")
public class CRUDAuthorTest extends BaseTest {
    private Author expA = testData.authors().getRandomOne();
    private final String expectedCountry = "Ukraine";
    private final String expectedCity = "Lviv";

    @Test(description = "Create author test")
    public void postAuthor() {
        client.post(POST_AUTHOR_SINGLE_OBJ, expA);
        setStatusCode(client.getResponse().getStatusCode());// post author
        String body = client.getResponse().getBody();          // get body from response as String

        Author actA = g.fromJson(body, Author.class);          // map response String to Author obj
        Assert.assertEquals(actA, expA);                       // verify that Author object is equal to expected
    }

    //make it depends from previous test because we use
    //post without checking it functionality so, if post
    //not working, it will be not possible to check get
    //TestNG will skip it
    @Test(description = "Get author test", dependsOnMethods = "postAuthor")
    public void getAuthor() {
        //precondition, author must exist in API data base
        client.post(POST_AUTHOR_SINGLE_OBJ, expA);

        // try to take out this on service layer
        client.get(String.format(GET_AUTHOR_SINGLE_OBJ, expA.getAuthorId()));
        setStatusCode(client.getResponse().getStatusCode());
        String body = client.getResponse().getBody();
        Author actA = g.fromJson(body, Author.class);

        // try to take out this on service layer in some validator class
        Assert.assertEquals(actA, expA);
    }

    @Test(description = "Update author test", dependsOnMethods = "postAuthor")
    public void testPutAuthor() {
        client.post(POST_AUTHOR_SINGLE_OBJ, expA);

        LocalDate date = LocalDate.of(2000, 1, 1);
        Birth expectedBirth = new Birth(date, expectedCountry, expectedCity);
        expA.setBirth(expectedBirth);
        client.put(String.format(PUT_AUTHOR_SINGLE_OBJ, expA.getAuthorId()), expA);
        setStatusCode(client.getResponse().getStatusCode());

        String body = client.getResponse().getBody();
        Author actA = g.fromJson(body, Author.class);

        Assert.assertEquals(actA.getBirth(), expA.getBirth());

    }

    @Test(description = "Delete author test", dependsOnMethods ="postAuthor")
    public void testDeleteAuthor() {
        client.post(POST_AUTHOR_SINGLE_OBJ, expA);
        client.delete(String.format(DELETE_AUTHOR_SINGLE_OBJ, expA.getAuthorId()));
        setStatusCode(client.getResponse().getStatusCode());
    }

    @Test(description = "Get all authors", dependsOnMethods = "postAuthor")
    public void testGetAllAuthors() {
        List<Author> authorsList = testData.authors().getSorted("authorId", "asc", 30);
        client.post(POST_AUTHOR_SINGLE_OBJ, authorsList);

        client.get(GET_ALL_AUTHORS_ARR);
        String body = client.getResponse().getBody();
        Author actA = g.fromJson(body, Author.class);
        
    }

    //and clear all posted data
    @AfterMethod(alwaysRun = true)
    public void cleanUp() {
        clean.authors();
    }
}
