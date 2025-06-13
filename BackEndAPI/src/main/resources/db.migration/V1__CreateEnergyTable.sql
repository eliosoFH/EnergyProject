CREATE TABLE energy_usage_hourly (
    hour TIMESTAMP PRIMARY KEY,
    community_produced NUMERIC,
    community_used NUMERIC,
    grid_used NUMERIC
);

CREATE TABLE current_percentage (
    hour TIMESTAMP PRIMARY KEY,
    community_depleted NUMERIC,  -- %
    grid_portion NUMERIC         -- %
);
