package ua.com.epam.config;

public interface URI {
    DataProp dp = new DataProp();
    String BASE_URI = dp.apiProtocol() + "://" + dp.apiHost() + ":" + dp.apiPort();

    //Author
    String GET_AUTHOR_SINGLE_OBJ = BASE_URI + "/api/library/author/%s";
    String GET_AUTHOR_OF_BOOK_OBJ = BASE_URI + "/api/library/book/%s/author";
    String GET_ALL_AUTHORS_ARR = BASE_URI + "/api/library/authors";
    String POST_AUTHOR_SINGLE_OBJ = BASE_URI + "/api/library/author/new";
    String PUT_AUTHOR_SINGLE_OBJ = BASE_URI + "/api/library/author/%s/update";
    String DELETE_AUTHOR_SINGLE_OBJ = BASE_URI + "/api/library/author/%s/delete";
}
