package fr.simplex_software.quarkus.camel.integrations.provider;

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
  public List<MoneyTransfer> getMoneyTransferOrders()
  {
    return moneyTransferMap.entrySet().stream().map(es -> es.getValue()).collect(Collectors.toList());
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
