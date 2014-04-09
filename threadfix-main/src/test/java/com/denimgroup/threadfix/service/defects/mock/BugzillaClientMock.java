package com.denimgroup.threadfix.service.defects.mock;

import com.denimgroup.threadfix.service.defects.util.TestConstants;
import com.denimgroup.threadfix.service.defects.utils.bugzilla.BugzillaClient;
import org.apache.xmlrpc.XmlRpcException;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertTrue;

/**
 * Created by denimgroup on 4/7/14.
 */
@SuppressWarnings("unchecked")
public class BugzillaClientMock implements BugzillaClient, TestConstants{

    ConnectionStatus status = ConnectionStatus.INVALID;

    public static final Map<String,String> versionMap = new HashMap<>();
    public static final Map<String, Object[]> products = new HashMap<>();
    public static final Map<String, Object[]> productMap = new HashMap<>();
    public static final Map<String, Integer> bugCreateMap = new HashMap<>();
    static {
        products.put("ids", new Object[]{1, 2, 3 ,4});
        productMap.put("products", new Object[]{new HashMap<String, Object>(), new HashMap<String, Object>(),
                new HashMap<String, Object>(), new HashMap<String, Object>()});
        Object[] products = productMap.get("products");
        fillProductMap(products[0], "TestProduct", 1);
        fillProductMap(products[1], "QA Testing", 2);
        fillProductMap(products[2], "No Component Project", 3);
        fillProductMap(products[3], "For ThreadFix", 4);

        bugCreateMap.put("id", 110);
        versionMap.put("version","4.2.1");
    }

    private static void fillProductMap(Object map, String name, int id) {
        ((HashMap<Object, Object>) map).put("name", name);
        ((HashMap<Object, Object>) map).put("id", id);
    }

    //TODO mock against BugzillaClientImpl.java, check return values for all methods

    @Override
    public ConnectionStatus configure(String url, String username, String password) {
        if (url.equals(BUGZILLA_BASE_URL + "/xmlrpc.cgi") && BUGZILLA_USERNAME.equals(username) &&
                BUGZILLA_PASSWORD.equals(password)) {
            status = ConnectionStatus.VALID;
            return ConnectionStatus.VALID;
        }

        status = ConnectionStatus.INVALID;
        return ConnectionStatus.INVALID;
    }

    @Override
    public Map<String, Integer> createBug(Map<String, String> bugMap) throws XmlRpcException {
        assertTrue("Status is still invalid.", status == ConnectionStatus.VALID);
        return bugCreateMap;
    }

    @Override
    public Object executeMethod(String method) throws XmlRpcException {
        return executeMethod(method, new Object[]{});
    }

    @Override
    public Object executeMethod(String method, Object... params) throws XmlRpcException {
        if (method.equals("Bugzilla.version")) {
            return versionMap;
        }

        assertTrue("Status is still invalid.", status == ConnectionStatus.VALID);
        if (method.equals("Product.get_accessible_products")) {
            return products;
        }

        if (method.equals("Product.get")) {

            if (params[0] instanceof HashMap<?, ?>) {
                Object[] names = (Object[]) ((HashMap<Object, Object>) params[0]).get("names");

                if (names == null) { // this means that the ids hash was passed in (probably) and we're in getProductNamesTest
                    return productMap;
                } else if (names.length == 1 && names[0].equals(TestConstants.BUGZILLA_PROJECT)) {
                    return productMap; // TODO return the expected map for valid project lookup
                } else { // this means it's the wrong project name
                    return null; // TODO return null or whatever
                }
            }
        }

        return null;
    }

    @Override
    public boolean checkUrl(String url) {
        return url.equals(BUGZILLA_BASE_URL + "/xmlrpc.cgi");
    }
}
