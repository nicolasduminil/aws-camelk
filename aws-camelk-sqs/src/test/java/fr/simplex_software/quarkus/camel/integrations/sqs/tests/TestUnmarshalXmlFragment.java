package fr.simplex_software.quarkus.camel.integrations.sqs.tests;

import fr.simplex_software.quarkus.camel.integrations.jaxb.*;
import fr.simplex_software.quarkus.camel.integrations.sqs.*;
import org.junit.jupiter.api.*;

import javax.xml.bind.*;

import java.math.*;

import static org.assertj.core.api.Assertions.*;

public class TestUnmarshalXmlFragment
{
  @Test
  public void unmarshall() throws JAXBException
  {
    final String MONEY_TRANSFER = "<mon:moneyTransfer xmlns:mon=\"http://www.simplex-software.fr/money-transfer\">\n" +
      "  <mon:reference>Tech Repairs</mon:reference>\n" +
      "  <mon:sourceAccount accountID=\"SG01\" accountType=\"CHECKING\" sortCode=\"04004\" accountNumber=\"00050525712\" transCode=\"03000\">\n" +
      "    <mon:bank bankName=\"Société Générale\">\n" +
      "      <mon:bankAddresses streetName=\"av. de Paris\" streetNumber=\"18\" poBox=\"Cedex\" cityName=\"Montmorency\" zipCode=\"95800\" countryName=\"France\"/>\n" +
      "    </mon:bank>\n" +
      "  </mon:sourceAccount>\n" +
      "  <mon:targetAccount accountID=\"ING01\" accountType=\"SAVINGS\" sortCode=\"08080\" accountNumber=\"425091789\" transCode=\"7BRE0\">\n" +
      "    <mon:bank bankName=\"ING\">\n" +
      "      <mon:bankAddresses streetName=\"blvd. Waterloo\" streetNumber=\"381\" poBox=\"INGPOBOX\" cityName=\"Waterloo\" zipCode=\"B1000\" countryName=\"Belgium\"/>\n" +
      "    </mon:bank>\n" +
      "  </mon:targetAccount>\n" +
      "  <mon:amount>1000.00</mon:amount>\n" +
      "</mon:moneyTransfer>";
    MoneyTransfer moneyTransfer = new UnmarshalXmlFragment().unmarshall(MONEY_TRANSFER);
    assertThat(moneyTransfer).isNotNull();
    assertThat(moneyTransfer.getReference()).isEqualTo("Tech Repairs");
    assertThat(moneyTransfer.getAmount()).isEqualTo(new BigDecimal("1000.00"));
  }
}
