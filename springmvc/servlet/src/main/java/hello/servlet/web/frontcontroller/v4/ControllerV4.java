package hello.servlet.web.frontcontroller.v4;

import java.util.Map;


public interface ControllerV4 {

    /**
     * @param ParamMap
     * @param model
     * @return
     */
    String process(Map<String, String> ParamMap, Map<String, Object> model);
}
