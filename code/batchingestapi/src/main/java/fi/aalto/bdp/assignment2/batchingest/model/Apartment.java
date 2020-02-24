package fi.aalto.bdp.assignment2.batchingest.model;

import com.univocity.parsers.annotations.Headers;
import com.univocity.parsers.annotations.Parsed;
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
@Headers(sequence = {"id", "name", "host_id", "host_name", "neighbourhood_group", "neighbourhood", "latitude", "longitude",
        "room_type", "price", "minimum_nights", "number_of_reviews", "last_review", "reviews_per_month", "calculated_host_listings_count", "availability_365"},
        extract = true)
public class Apartment {

    @PrimaryKeyColumn(ordinal = 2, type = PrimaryKeyType.PARTITIONED)
    @Parsed(field = "id", defaultNullRead = "0")
    private Long id;

    @Parsed(field = "name", defaultNullRead = " ")
    private String name;

    @Parsed(field = "host_id", defaultNullRead = "0")
    @Column("host_id")
    private Long hostId;

    @Parsed(field = "host_name", defaultNullRead = " ")
    @Column("host_name")
    private String hostName;

    @Parsed(field = "neighbourhood_group",defaultNullRead = " ")
    @Column("neighbourhood_group")
    private String neighbourhoodGroup;

    @PrimaryKeyColumn(ordinal = 4, type = PrimaryKeyType.CLUSTERED)
//    @PrimaryKeyColumn(ordinal = 3)
    @Parsed(field = "neighbourhood", defaultNullRead = " ")
    private String neighbourhood;

    @Parsed(field = "latitude", defaultNullRead = " ")
    private String latitude;

    @Parsed(field = "longitude", defaultNullRead = " ")
    private String longitude;

    @Parsed(field = "room_type", defaultNullRead = " ")
    @PrimaryKeyColumn(value = "room_type", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String roomType;

    @Parsed(field = "price", defaultNullRead = "0")
    private Long price;

    @Parsed(field = "minimum_nights", defaultNullRead = "0")
    @PrimaryKeyColumn(value = "minimum_nights", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private Long minimumNights;

    @Parsed(field = "number_of_reviews", defaultNullRead = "0")
    @Column("number_of_reviews")
    private Long numberOfReviews;

    @Parsed(field = "last_review", defaultNullRead = " ")
    @PrimaryKeyColumn(value = "last_review", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    private String lastReview;

    @Parsed(field = "reviews_per_month", defaultNullRead = " ")
    @Column("reviews_per_month")
    private String reviewPerMonth;

    @Parsed(field = "calculated_host_listings_count", defaultNullRead = "")
    @Column("calculated_host_listings_count")
    private String calculatedHostListingsCount;

    @Parsed(field = "availability_365", defaultNullRead = "0")
    @Column("availability_365")
    private Long availability365;
}
