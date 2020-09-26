package adapter.messages;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * POJO model of the result of data enrichment received from service A
 * and weather service, which will be converted to JSON and sent to service B.
 *
 */
public class MsgB {

    /**
     * Some text message from service A message body
     */
    private String txt;

    /**
     * Timestamp
     */
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm a z")
    private Date createdDt;

    /**
     * Current Temp.
     */
    private Integer currentTemp;

    /* Getters and Setters */
    public String getTxt() {
        return txt;
    }
    public void setTxt(String txt) {
        this.txt = txt;
    }
    public Date getCreatedDt() {
        return createdDt;
    }
    public Integer getCurrentTemp() {
        return currentTemp;
    }
    public void setCurrentTemp(Integer currentTemp) {
        this.currentTemp = currentTemp;
    }
    public void setCreatedDt(Date createdDt) {
        this.createdDt = createdDt;
    }
}
