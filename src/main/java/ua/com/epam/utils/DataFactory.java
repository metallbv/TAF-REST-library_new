package ua.com.epam.utils;

import ua.com.epam.utils.data.service.AuthorData;

public class DataFactory {

    public AuthorData authors() {
        return new AuthorData();
    }
}