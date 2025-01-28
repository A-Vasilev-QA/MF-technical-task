package qa.avasilev.models;

import lombok.Data;

@Data
public class CreateUserNegativeResponseModel {
    boolean success;
    String[] message;
}
