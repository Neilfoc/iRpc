package irpc.interfaces;

import java.util.List;

/**
 * @author neilfoc
 * @Description
 * @Date 2022/5/8
 */
public interface DataService {

    /**
     * 发送数据
     *
     * @param body
     */
    String sendData(String body);

    /**
     * 获取数据
     *
     * @return
     */
    List<String> getList();
}
