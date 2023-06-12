package iri.events;

import java.sql.*;
import java.util.Properties;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKBWriter;

public class DbConnector {

    String url;
    Properties props;
    Connection conn;

    public DbConnector(String url, String username, String password) {
        this.url = url; // e.g. "jdbc:postgresql://localhost/test?user=fred&password=secret&ssl=true"
        props = new Properties();
        props.setProperty("user",username);
        props.setProperty("password",password);
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, props.getProperty("user"), props.getProperty("password"));
    }

    /*private static byte[] convertToWKB(Geometry g) {
        boolean includeSRID = g.getSRID() != 0;
        int dimensionCount = getDimensionCount(g);
        WKBWriter writer = new WKBWriter(dimensionCount, includeSRID);
        return writer.write(g);
    }*/

    public long insertProduct(Product product) {
        String SQL = "INSERT INTO product(id,product_id,timestamp,wkb_geometry) "
                + "VALUES(?,?,?,ST_Transform(?,3857))"; // TODO: add constraint on id and then here add "ON CONFLICT (id) DO NOTHING";

        long id = 0;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, product.getIri_id());
            pstmt.setLong(2, product.getProduct_id());
            pstmt.setTimestamp(3, product.getIritimestamp());
            GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
            Coordinate coord = new Coordinate(product.getLongitude(), product.getLatitude());
            Point point = geometryFactory.createPoint(coord);
            point.setSRID(4386);

            //ReprojectingFeatureCollection rfc = new ReprojectingFeatureCollection(features, CRS.decode("epsg:3875"));
            WKBWriter wkbWriter = new WKBWriter(2, true);
            pstmt.setBytes(4, wkbWriter.write(point));


            int affectedRows = pstmt.executeUpdate();
            // check the affected rows 
            if (affectedRows > 0) {
                // get the ID back
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getLong(1);
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return id;
    }


}
