package com.packleader.rapid;

import org.mockito.MockitoAnnotations;
import org.powermock.modules.testng.PowerMockTestCase;
import org.powermock.reflect.Whitebox;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

import java.util.function.Supplier;

import static org.testng.Assert.assertSame;

public class BaseTest extends PowerMockTestCase {

    @BeforeMethod
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    protected <T> void testGetter(Object object, String fieldName, T value, Supplier<T> getter) {
        Whitebox.setInternalState(object, fieldName, value);

        testGetter(value, getter);
    }

    protected <T> void testGetter(T expectedValue, Supplier<T> getter) {
        T result = getter.get();

        assertSame(result, expectedValue);
    }

    @DataProvider(name = "stringProvider")
    protected Object[][] stringProvider() {
        return new Object[][]{
                {"string1"},
                {"anotherString"},
                {null},
                {""}
        };
    }

    @DataProvider(name = "booleanProvider")
    protected Object[][] booleanProvider() {
        return new Object[][]{
                {true},
                {false}
        };
    }
}
