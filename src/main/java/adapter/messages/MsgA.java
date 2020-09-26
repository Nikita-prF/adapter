package adapter.messages;
import adapter.coordinateValidator.CoordinateConstraint;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

/**
 * POJO model for validation of messages received from A service
 *
 * For correct validation of latitude and longitude values I created
 * a special Constraint class implemented in the standard javax validation interface.
 *
 * {@link adapter.coordinateValidator.CoordinateValidator}
 */
public class MsgA {

    /**
     * Some text message
     */
    @NotNull(message = "Please enter a message")
    @Size(min = 1, message = "Message must have at least one character")
    private String msg;

    /**
     * Message Language
     */
    @NotNull(message = "Please choose a language")
    private String lng;

    /**
     * Fields of longitude and latitude values
     */
    @NotNull(message = "Please enter the coordinates")
    @CoordinateConstraint
    private Map<String, String> coordinates;


    /* Getters and Setters */
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getLng() {
        return lng;
    }
    public void setLng(String lng) {
        this.lng = lng;
    }
    public Map<String, String> getCoordinates() {
        return coordinates;
    }
    public void setCoordinates(Map<String, String> coordinates) {
        this.coordinates = coordinates;
    }
    public String getLatitude() {
        return coordinates.get("latitude");
    }
    public String getLongitude() {
        return coordinates.get("longitude");
    }

    /* Method for POJO model converting */
    public String toString() {
        return String.format("msg : %s\n lng : %s\n lat : %s\n lon : %s",
                getMsg(), getLng(), getLatitude(), getLongitude());
    }
}
