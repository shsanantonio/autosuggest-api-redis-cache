package chekins.com.test2.model;

import com.redis.om.spring.annotations.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;

@Data
@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor
@Document
public class Location {
    @Id
    private String id;
    @NonNull
    private String count;
    @NonNull
    @AutoComplete
  //  @Searchable
    private String name;
    @NonNull
    @AutoCompletePayload("name")
    private String fullName;
    @NonNull
    @AutoCompletePayload("name")
    private String type;

    public Location(@NonNull String name, @NonNull String fullName, @NonNull String type, @NonNull String state, @NonNull String country, @NonNull String hierarchyPath) {
        this.name = name;
        this.fullName = fullName;
        this.type = type;
        this.state = state;
        this.country = country;
        this.hierarchyPath = hierarchyPath;
    }

    @NonNull
    private String state;
    @NonNull
    private String country;
    @NonNull
    private String hierarchyPath;
    @NonNull
    @AutoCompletePayload("name")
    @GeoIndexed
    private Point loc;

}