package fi.aalto.bdp.assignmenttwo.streamingest.client.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Apartment {

    @PrimaryKeyColumn(ordinal = 2, type = PrimaryKeyType.PARTITIONED)
    private Long id;

    private String name;

    @Column("host_id")
    private Long hostId;

    @Column("host_name")
    private String hostName;

    @Column("neighbourhood_group")
    private String neighbourhoodGroup;

    @PrimaryKeyColumn(ordinal = 4, type = PrimaryKeyType.CLUSTERED)
    private String neighbourhood;

    private String latitude;

    private String longitude;

    @PrimaryKeyColumn(value = "room_type", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String roomType;

    private Long price;

    @PrimaryKeyColumn(value = "minimum_nights", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private Long minimumNights;

    @Column("number_of_reviews")
    private Long numberOfReviews;

    @PrimaryKeyColumn(value = "last_review", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    private String lastReview;

    @Column("reviews_per_month")
    private String reviewPerMonth;

    @Column("calculated_host_listings_count")
    private String calculatedHostListingsCount;

    @Column("availability_365")
    private Long availability365;
}
