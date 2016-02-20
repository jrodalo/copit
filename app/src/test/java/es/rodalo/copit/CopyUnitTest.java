package es.rodalo.copit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import es.rodalo.copit.utils.Error;
import es.rodalo.copit.utils.Files;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests relacionados con la acción de copiar archivos
 */
public class CopyUnitTest {

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();


    @Test(expected = Error.NoSourceException.class)
    public void should_throw_exception_when_source_folder_doesnt_exists() throws Exception {

        File source = new File("random_source_folder");
        File dest = tempFolder.newFolder("test-dest");

        Files.copyFolder(source, dest, null);
    }


    @Test(expected = Error.NoDestinationException.class)
    public void should_throw_exception_when_dest_folder_doesnt_exists() throws Exception {

        File source = tempFolder.newFolder("test-source");
        File dest = new File("random_dest_folder");

        Files.copyFolder(source, dest, null);
    }


    @Test(expected = Error.SameDirectoryException.class)
    public void should_throw_exception_when_both_folders_are_the_same() throws Exception {

        File source = tempFolder.newFolder("test-source");

        Files.copyFolder(source, source, null);
    }



    @Test(expected = Error.IsChildException.class)
    public void should_throw_exception_when_one_folder_contains_the_other() throws Exception {

        File source = tempFolder.newFolder("test-source");
        File dest = new File(source, "test-dest");

        assertThat(dest.mkdir(), is(true));

        Files.copyFolder(source, dest, null);
    }


    @Test
    public void should_copy_files_to_correct_destination() throws Exception {

        File source = tempFolder.newFolder("test-source");
        File dest = tempFolder.newFolder("test-dest");

        File[] files = createFiles(
                new File(source, "image1.png"),
                new File(source, "image2.png")
        );

        assertThat(source.listFiles().length, is(files.length));
        assertThat(dest.listFiles().length, is(0));

        Files.copyFolder(source, dest, null);

        assertThat(dest.listFiles().length, is(files.length));
    }


    @Test
    public void should_retain_last_modified_dates() throws Exception {

        File source = tempFolder.newFolder("test-source");
        File dest = tempFolder.newFolder("test-dest");

        File image = new File(source, "image1.png");

        assertThat(image.createNewFile(), is(true));

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -5);
        assertThat(image.setLastModified(cal.getTimeInMillis()), is(true));

        Files.copyFolder(source, dest, null);

        assertThat(dest.listFiles().length, is(1));
        assertThat(dest.listFiles()[0].lastModified(), is(image.lastModified()));
    }


    private File[] createFiles(File ... files) throws IOException {

        for (File file : files) {
            if (!file.createNewFile()) {
                throw new IOException("Cant create file " + file);
            }
        }

        return files;
    }

}

