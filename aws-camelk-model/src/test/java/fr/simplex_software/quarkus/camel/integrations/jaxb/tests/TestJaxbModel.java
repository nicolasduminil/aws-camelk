package fr.simplex_software.quarkus.camel.integrations.jaxb.tests;

import fr.simplex_software.quarkus.camel.integrations.jaxb.*;
import org.junit.jupiter.api.*;

import javax.xml.bind.*;
import java.io.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

public class TestJaxbModel
{
  @Test
  public void testUnmarshalMoneyTransfersFile() throws JAXBException
  {
    MoneyTransfers moneyTransfers = (MoneyTransfers) unmarshalXmlFile(new File("src/test/resources/money-transfers.xml"));
    assertThat(moneyTransfers).isNotNull();
    List<MoneyTransfer> moneyTransferList = moneyTransfers.getMoneyTransfers();
    assertThat(moneyTransferList).isNotNull();
    assertThat(moneyTransferList.size()).isEqualTo(5);
    MoneyTransfer moneyTransfer = moneyTransferList.get(3);
    assertThat(moneyTransfer.getReference()).isEqualTo("Meal and Entertainment");
    assertThat(moneyTransfer.getSourceAccount().getAccountID()).isEqualTo("SG01");
  }

  @Test
  public void testUnmarshalMoneyTransferFile() throws JAXBException
  {
    MoneyTransfer moneyTransfer = (MoneyTransfer) unmarshalXmlFile(new File("src/test/resources/money-transfer.xml"));
    assertThat(moneyTransfer).isNotNull();
    assertThat(moneyTransfer.getReference()).isEqualTo("Tech Repairs");
    assertThat(moneyTransfer.getSourceAccount().getAccountID()).isEqualTo("SG01");
  }

  @Test
  public void testMarshalMoneyTransfers() throws JAXBException
  {
    MoneyTransfers moneyTransfers = (MoneyTransfers) unmarshalXmlFile(new File("src/test/resources/money-transfers.xml"));
    assertThat(moneyTransfers).isNotNull();
    List<MoneyTransfer> moneyTransferList = moneyTransfers.getMoneyTransfers();
    assertThat(marshalMoneyTransfersToXmlFile(moneyTransfers, new File ("src/test/resources/xfer.xml"))).exists();
  }

  @Test
  public void testMarshalMoneyTransfer() throws JAXBException
  {
    MoneyTransfer moneyTransfer = (MoneyTransfer) unmarshalXmlFile(new File("src/test/resources/money-transfer.xml"));
    assertThat(moneyTransfer).isNotNull();
    assertThat(marshalMoneyTransfersToXmlFile(moneyTransfer, new File ("src/test/resources/xfer2.xml"))).exists();
  }


  private Object unmarshalXmlFile (File xml) throws JAXBException
  {
    return JAXBContext.newInstance(MoneyTransfers.class).createUnmarshaller().unmarshal(xml);
  }


  private File marshalMoneyTransfersToXmlFile(Object moneyTransfers, File xml)
  {
    try
    {
      Marshaller marshaller = JAXBContext.newInstance(moneyTransfers.getClass()).createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(moneyTransfers, xml);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return xml;
  }
}
