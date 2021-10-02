package com.udacity.examples.Testing;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class HelperTest {

    @Test
    public void getCount() {
        List<String> empNames = Arrays.asList("sareeta", "udacity");
        final long actual = Helper.getCount(empNames);
        assertEquals(2, actual);
    }
	
}
