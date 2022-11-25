package cz.muni.ics.spreg_sync.base.adapters;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface PerunAdapterRpc {

    JsonNode getFacilitiesByAttributeWithAttributes (String attributeName, List<String> attrNames);

}
