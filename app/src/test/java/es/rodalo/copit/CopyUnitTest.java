package es.rodalo.copit;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

/**
 * Tests relacionados con la acción de copiar archivos
 */
public class CopyUnitTest {

    private File createTempFolder(String name) {

        File temp = FileUtils.getTempDirectory();

        File source = new File(temp, name);

        source.mkdir();

        return source;
    }


    @Test(expected = IOException.class)
    public void should_throw_exception_when_source_folder_doesnt_exists() throws Exception {

        MainActivity activity = new MainActivity();

        File source = new File("random_source_folder");
        File dest = createTempFolder("test-dest");

        assertThat(activity.canExecuteCopy(source, dest), is(false));
    }


    @Test(expected = IOException.class)
    public void should_throw_exception_when_dest_folder_doesnt_exists() throws Exception {

        MainActivity activity = new MainActivity();

        File source = createTempFolder("test-source");
        File dest = new File("random_dest_folder");

        assertThat(activity.canExecuteCopy(source, dest), is(false));
    }


    @Test(expected = IOException.class)
    public void should_throw_exception_when_both_folders_are_the_same() throws Exception {

        MainActivity activity = new MainActivity();

        File source = createTempFolder("test-source");

        assertThat(activity.canExecuteCopy(source, source), is(false));
    }


}

