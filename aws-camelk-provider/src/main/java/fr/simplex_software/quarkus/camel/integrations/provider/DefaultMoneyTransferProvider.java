package fr.simplex_software.quarkus.camel.integrations.provider;

import fr.simplex_software.quarkus.camel.integrations.api.*;
import fr.simplex_software.quarkus.camel.integrations.jaxb.*;

import javax.enterprise.context.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

@ApplicationScoped
public class DefaultMoneyTransferProvider implements MoneyTransferFacade
{
  private Map<String, MoneyTransfer> moneyTransferMap = new ConcurrentHashMap<>();

  @Override
  public MoneyTransfers getMoneyTransferOrders()
  {
    System.out.println ("$$$ getMoneyTransferOrders()");
    Set<Map.Entry<String, MoneyTransfer>> entries = moneyTransferMap.entrySet();
    if (entries.isEmpty())
      System.out.println ("$$$ Entrie is empty");
    else
      System.out.println ("$$$ Entrie is not empty");
    entries.forEach(e -> System.out.println ("$$$ Entry: " + e.getKey() + "->" + e.getValue()));
    List<MoneyTransfer> moneyTransferList = entries.stream().map(es -> es.getValue()).collect(Collectors.toList());
    moneyTransferList.forEach(mt -> System.out.println ("$$$ Mt: " + mt.getReference()));
    return new MoneyTransfers(moneyTransferList);
    //return new MoneyTransfers(moneyTransferMap.entrySet().stream().map(es -> es.getValue()).collect(Collectors.toList()));
  }

  @Override
  public Optional<MoneyTransfer> getMoneyTransferOrder(String reference)
  {
    return Optional.ofNullable(moneyTransferMap.get(reference));
  }

  @Override
  public String createMoneyTransferOrder(MoneyTransfer moneyTransfer)
  {
    String ref = moneyTransfer.getReference();
    moneyTransferMap.put(ref, moneyTransfer);
    return ref;
  }

  @Override
  public Long updateMoneyTransferOrder(String ref, MoneyTransfer moneyTransfer)
  {
    return null;
  }

  @Override
  public void deleteMoneyTransferOrder(String ref)
  {

  }
}
