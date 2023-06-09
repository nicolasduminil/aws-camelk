package fr.simplex_software.quarkus.camel.integrations.api;

import fr.simplex_software.quarkus.camel.integrations.jaxb.*;

import java.io.*;
import java.util.*;

public interface MoneyTransferFacade extends Serializable
{
  List<MoneyTransfer> getMoneyTransferOrders();
  Optional<MoneyTransfer> getMoneyTransferOrder(String reference);
  String createMoneyTransferOrder(MoneyTransfer moneyTransfer);
  void updateMoneyTransferOrder(String ref, MoneyTransfer moneyTransfer);
  void deleteMoneyTransferOrder(String ref);
}
