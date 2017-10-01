package com.packleader.rapid.generator;

import com.packleader.rapid.BaseTest;
import com.packleader.rapid.config.CodeGeneratorConfig;
import com.packleader.rapid.config.DocGeneratorConfig;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testng.annotations.Test;

public class RapidGeneratorTest extends BaseTest {

    @Mock
    private DocGenerator docGeneratorMock;

    @Mock
    private CodeGenerator codeGeneratorMock;

    @Mock
    private DocGeneratorConfig docGeneratorConfigMock;

    @Mock
    private CodeGeneratorConfig codeGeneratorConfigMock;

    @InjectMocks
    private RapidGenerator rapidGenerator;

    @Test
    public void testGenerate() throws Exception {
        rapidGenerator.generate(docGeneratorConfigMock, codeGeneratorConfigMock);

        Mockito.verify(docGeneratorMock).generate(docGeneratorConfigMock);
        Mockito.verify(codeGeneratorMock).generate(codeGeneratorConfigMock);
    }
}
