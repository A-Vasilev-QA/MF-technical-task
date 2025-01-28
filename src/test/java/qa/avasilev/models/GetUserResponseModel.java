package qa.avasilev.models;

import lombok.Data;

@Data
public class GetUserResponseModel {
    String username,
            email,
            password,
            created_at,
            updated_at;
    int id;
}
