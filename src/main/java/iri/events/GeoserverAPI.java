package iri.events;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.spatial.BBOX;
import org.opengis.geometry.Envelope;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class GeoserverAPI {
    URI capabilitiesURI;

    public DataStore getDatastore() {
        return datastore;
    }

    DataStore datastore;

    public GeoserverAPI(String capabilities) {
        // using geoserver from isc project
        this.capabilitiesURI = URI.create(capabilities);
        Map<String,String> params = new HashMap<>();
        params.put("WFSDataStoreFactory:GET_CAPABILITIES_URL", getCapabilitiesURI().toString());
        try {
            datastore = DataStoreFinder.getDataStore(params);

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(datastore);
        // Step 3 - discovery
        String typeNames[] = new String[0];
        try {
            typeNames = datastore.getTypeNames();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String typeName = typeNames[0];
        SimpleFeatureType schema = null;
        try {
            schema = datastore.getSchema( typeName );
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Step 4 - target
        FeatureSource<SimpleFeatureType, SimpleFeature> source = null;
        try {
            source = datastore.getFeatureSource( typeName );
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            System.out.println( "Metadata Bounds: " + source.getBounds() );
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Step 5 - query
        try {
            String geomName = schema.getGeometryDescriptor().getLocalName();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        BBOX bbox =
//        Envelope bbox = new Envelope(5.8358140744676303, 45.659168946713827, 10.979311848153316, 47.869910020393519);


    }


    public URI getCapabilitiesURI() {
        return capabilitiesURI;
    }

    public void setCapabilitiesURI(URI capabilitiesURI) {
        this.capabilitiesURI = capabilitiesURI;
    }
}
