package ru.practicum.shareit.request;

import lombok.Data;


import java.util.Date;

/**
 * TODO Sprint add-item-requests.
 */

@Data
public class ItemRequest {
    private long id;
    private String description;
    private long requestor;
    private Date created;
}
