package com.packleader.rapid.plugin;

import com.packleader.rapid.BaseTest;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.mockito.Mockito.inOrder;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.testng.Assert.assertSame;

@PrepareForTest({ZipFileHelper.class})
public class ZipFileHelperTest extends BaseTest {

    @InjectMocks
    private ZipFileHelper zipFileHelper;

    @Test
    public void testUnpackDependencies() throws Exception {
        mockStatic(Files.class);
        final String zipEntryName1 = "skipMe";
        final String zipEntryName2 = "thisIsAReallyLongName";
        final String zipEntryName3 = "shortName";
        final String zipEntryName4 = "invalidPath";
        final String destinationFilePath = "destinationDirectory";
        File sourceMock = mock(File.class);
        File destinationFileMock = mock(File.class);
        ZipFile zipFileMock = mock(ZipFile.class);
        ZipEntry zipEntryMock1 = mockZipEntry(zipEntryName1, false);
        ZipEntry zipEntryMock2 = mockZipEntry(zipEntryName2, true);
        ZipEntry zipEntryMock3 = mockZipEntry(zipEntryName3, false);
        ZipEntry zipEntryMock4 = mockZipEntry(zipEntryName4, false);
        Stream zipEntryStream = Stream.of(zipEntryMock1, zipEntryMock2, zipEntryMock3, zipEntryMock4);
        File destFileMock2 = mockFile(destinationFilePath, "destFilePath2");
        File destFileMock3 = mockFile(destinationFilePath, "destFilePath3");
        File destFileMock4 = mockFile("invalidPath", "destFilePath4");
        InputStream inputStreamMock = mock(InputStream.class);
        Path destinationPathMock = mock(Path.class);
        ArgumentCaptor<InputStream> inputStreamCaptor = ArgumentCaptor.forClass(InputStream.class);
        ArgumentCaptor<Path> pathCaptor = ArgumentCaptor.forClass(Path.class);

        whenNew(ZipFile.class).withArguments(sourceMock).thenReturn(zipFileMock);
        when(zipFileMock.stream()).thenReturn(zipEntryStream);
        whenNew(File.class).withArguments(destinationFileMock, zipEntryName2).thenReturn(destFileMock2);
        whenNew(File.class).withArguments(destinationFileMock, zipEntryName3).thenReturn(destFileMock3);
        whenNew(File.class).withArguments(destinationFileMock, zipEntryName4).thenReturn(destFileMock4);
        when(destinationFileMock.getCanonicalPath()).thenReturn(destinationFilePath);
        when(zipFileMock.getInputStream(zipEntryMock3)).thenReturn(inputStreamMock);
        when(destFileMock3.toPath()).thenReturn(destinationPathMock);
        when(Files.copy(inputStreamCaptor.capture(), pathCaptor.capture())).thenReturn(0l);

        zipFileHelper.unpack(sourceMock, destinationFileMock, e -> e != zipEntryMock1);

        Mockito.verify(zipEntryMock1, Mockito.never()).isDirectory();
        Mockito.verify(destFileMock2).mkdirs();
        assertSame(inputStreamCaptor.getValue(), inputStreamMock);
        assertSame(pathCaptor.getValue(), destinationPathMock);
        Mockito.verify(zipEntryMock4, Mockito.never()).isDirectory();

        InOrder inOrder = inOrder(zipEntryMock3, zipEntryMock2);
        inOrder.verify(zipEntryMock3).isDirectory();
        inOrder.verify(zipEntryMock2).isDirectory();
    }

    private ZipEntry mockZipEntry(String name, boolean isDirectory) {
        ZipEntry zipEntryMock = mock(ZipEntry.class);
        when(zipEntryMock.getName()).thenReturn(name);
        when(zipEntryMock.isDirectory()).thenReturn(isDirectory);
        return zipEntryMock;
    }

    private File mockFile(String destinationFilePath, String canonicalPath) throws IOException {
        File fileMock = mock(File.class);
        canonicalPath = destinationFilePath + File.separator + canonicalPath;
        when(fileMock.getCanonicalPath()).thenReturn(canonicalPath);
        return fileMock;
    }

}