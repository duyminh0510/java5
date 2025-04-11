package assignment_java5.java5.shippercontroller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/proxy")
public class ProxyController {

    @Autowired
    private RestTemplate restTemplate;

    private static final String BASE_URL = "https://provinces.open-api.vn/api";

    @GetMapping("/provinces")
    public ResponseEntity<?> getProvinces() {
        String url = BASE_URL + "/?depth=1";
        Object[] provinces = restTemplate.getForObject(url, Object[].class);
        return ResponseEntity.ok(provinces);
    }

    @GetMapping("/province/{code}")
    public ResponseEntity<?> getProvinceByCode(@PathVariable String code) {
        String url = BASE_URL + "/p/" + code + "?depth=2";
        return ResponseEntity.ok(restTemplate.getForObject(url, Object.class));
    }

    @GetMapping("/district/{code}")
    public ResponseEntity<?> getDistrictByCode(@PathVariable String code) {
        String url = BASE_URL + "/d/" + code + "?depth=2";
        return ResponseEntity.ok(restTemplate.getForObject(url, Object.class));
    }
}
