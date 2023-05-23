package fr.simplex_software.quarkus.camel.integrations.sqs;

import fr.simplex_software.quarkus.camel.integrations.jaxb.*;

import javax.xml.bind.*;
import java.io.*;

public class UnmarshalXmlFragment
{
  public MoneyTransfer unmarshall (String xml) throws JAXBException
  {
    return (MoneyTransfer) JAXBContext.newInstance(MoneyTransfer.class).createUnmarshaller().unmarshal(new StringReader(xml));
  }
}
