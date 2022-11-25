package cz.muni.ics.spreg_sync.base.adapters.impl;

import com.fasterxml.jackson.databind.JsonNode;
import cz.muni.ics.spreg_sync.base.adapters.PerunAdapterRpc;
import cz.muni.ics.spreg_sync.base.connectors.PerunConnectorRpc;
import cz.muni.ics.spreg_sync.base.properties.AttributesProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class PerunAdapterRpcImpl implements PerunAdapterRpc {

    public static final String ATTRIBUTE_NAME = "attributeName";
    public static final String ATTRIBUTE_VALUE = "attributeValue";
    public static final String ATTR_NAMES = "attrNames";

    public static final String FACILITIES_MANAGER = "facilitiesManager";
    public static final String GET_FACILITIES_BY_ATTRIBUTE_WITH_ATTRIBUTES = "getFacilitiesByAttributeWithAttributes";

    private final PerunConnectorRpc connectorRpc;

    @Autowired
    public PerunAdapterRpcImpl(PerunConnectorRpc connectorRpc)
    {
        this.connectorRpc = connectorRpc;
    }

    @Override
    public JsonNode getFacilitiesByAttributeWithAttributes(String attributeName, List<String> attrNames) {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put(ATTRIBUTE_NAME, attributeName);
        map.put(ATTRIBUTE_VALUE, true);
        map.put(ATTR_NAMES, attrNames);

        return connectorRpc.post(FACILITIES_MANAGER, GET_FACILITIES_BY_ATTRIBUTE_WITH_ATTRIBUTES, map);
    }
}
