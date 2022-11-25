package cz.muni.ics.spreg_sync.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.ics.spreg_sync.base.adapters.PerunAdapterRpc;
import cz.muni.ics.spreg_sync.base.properties.AttributesProperties;
import cz.muni.ics.spreg_sync.persistence.SpRegSyncDao;
import cz.muni.ics.spreg_sync.service.SpRegSyncService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SpRegSyncServiceImpl implements SpRegSyncService {

    public static String ENTITY_ID_ATTRIBUTE = "urn:perun:facility:attribute-def:def:entityID";
    public static String CLIENT_ID_ATTRIBUTE = "urn:perun:facility:attribute-def:def:OIDCClientID";
    public static String IS_SAML_FACILITY_ATTRIBUTE = "urn:perun:facility:attribute-def:def:isSamlFacility";
    public static String IS_OIDC_FACILITY_ATTRIBUTE = "urn:perun:facility:attribute-def:def:isOidcFacility";

    public static String SAML = "saml";
    public static String OIDC = "oidc";
    public static String ID = "_id";
    public static String FACILITY = "facility";
    public static String DESCRIPTION = "description";
    public static String ATTRIBUTES = "attributes";
    public static String NAMESPACE = "namespace";
    public static String FRIENDLY_NAME = "friendlyName";
    public static String VALUE = "value";
    public static String LAST_UPDATE = "last-update";

    public static String CREATED = "created";
    public static String UPDATED = "updated";
    public static String DELETED = "deleted";

    private final SpRegSyncDao spRegSyncDao;
    private final PerunAdapterRpc perunAdapterRpc;
    private final AttributesProperties attributesProperties;

    @Autowired
    public SpRegSyncServiceImpl(SpRegSyncDao spRegSyncDao,
                                PerunAdapterRpc perunAdapterRpc,
                                AttributesProperties attributesProperties) {
        this.spRegSyncDao = spRegSyncDao;
        this.perunAdapterRpc = perunAdapterRpc;
        this.attributesProperties = attributesProperties;
    }

    @Override
    public Document sync() throws JsonProcessingException {
        Document changes = new Document();

        Set<Document> samlFacilitiesWithAttributes = getFacilitiesByAttributeWithAttributes(IS_SAML_FACILITY_ATTRIBUTE);
        Document samlChanges = processFacilities(samlFacilitiesWithAttributes, SAML);
        if (!samlChanges.isEmpty()) {
            changes.put(SAML, samlChanges);
        }

        Set<Document> oidcFacilitiesWithAttributes = getFacilitiesByAttributeWithAttributes(IS_OIDC_FACILITY_ATTRIBUTE);
        Document oidcChanges = processFacilities(oidcFacilitiesWithAttributes, OIDC);
        if (!oidcChanges.isEmpty()) {
            changes.put(OIDC, oidcChanges);
        }

        return changes.isEmpty() ? null : changes;
    }

    private Set<Document> getFacilitiesByAttributeWithAttributes(String conditionAttrName) throws JsonProcessingException {
        List<String> attributes = new ArrayList<>(attributesProperties.getAttrsToPersist());

        // facility description contains id attr value
        attributes.remove(ENTITY_ID_ATTRIBUTE);
        attributes.remove(CLIENT_ID_ATTRIBUTE);

        JsonNode result = perunAdapterRpc.getFacilitiesByAttributeWithAttributes(
                conditionAttrName,
                attributes
        );

        return getFacilitiesAsDocuments(result);
    }

    private Set<Document> getFacilitiesAsDocuments(JsonNode facilitiesAsJson) throws JsonProcessingException {
        Set<Document> result = new HashSet<>();
        ObjectMapper mapper = new ObjectMapper();

        for (JsonNode facilityJson : facilitiesAsJson) {
            Object json = mapper.readValue(facilityJson.toString(), Object.class);
            String facilityString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            Document facility = Document.parse(facilityString);

            Document doc = new Document();
            doc.put(ID, facility.get(FACILITY, Document.class).getString(DESCRIPTION));

            Document attributes = new Document();
            for (Document attribute : facility.getList(ATTRIBUTES, Document.class)) {
                attributes.put(attribute.getString(NAMESPACE) + ":" + attribute.getString(FRIENDLY_NAME), attribute.get(VALUE));
            }
            doc.put(ATTRIBUTES, attributes);

            result.add(doc);
        }

        return result;
    }

    private Document processFacilities(Set<Document> facilities, String collection) {
        Document changes = new Document();
        List<Document> created = new ArrayList<>();
        List<Document> updated = new ArrayList<>();
        List<Document> deleted = new ArrayList<>();

        Set<Document> facilitiesDb = spRegSyncDao.findAll(collection);

        for (Document facility : facilities) {
            Document facilityDb = spRegSyncDao.find(facility.getString(ID), collection);

            if (facilityDb == null) {
                facility.put(LAST_UPDATE, (new Date()).toInstant().toString());
                created.add(facility);
                spRegSyncDao.create(facility, collection);
            } else if (!facility.get(ATTRIBUTES, Document.class).equals(facilityDb.get(ATTRIBUTES, Document.class))) {
                facility.put(LAST_UPDATE, (new Date()).toInstant().toString());
                updated.add(facility);
                spRegSyncDao.update(facility, collection);
                facilitiesDb.remove(facilityDb);
            } else {
                facilitiesDb.remove(facilityDb);
            }
        }

        for (Document facility : facilitiesDb) {
            deleted.add(facility);
            spRegSyncDao.delete(facility, collection);
        }

        if (!created.isEmpty()) {
            changes.put(CREATED, created);
        }
        if (!updated.isEmpty()) {
            changes.put(UPDATED, updated);
        }
        if (!deleted.isEmpty()) {
            changes.put(DELETED, deleted);
        }

        return changes;
    }
}
