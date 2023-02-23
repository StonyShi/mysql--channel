# mysql 协议 binlog 解析


## SlaveConnector  远程连接 binlog event接收
```
SlaveConnector connector = new SlaveConnector(host, port, username, password);
connector.setServerId(5);

connector.addFilter(new EventFilter(new EventType[]{UPDATE_ROWS_EVENT, DELETE_ROWS_EVENT, WRITE_ROWS_EVENT}));
connector.addFilter(new DatabaseFilter(new String[]{"test"}));

connector.registerListener(event -> {
    try {
        System.out.println(event);
    } catch (Exception e) {
        e.printStackTrace();
    }
});
connector.registerListener(new EventListener() {
    @Override
    public void onEvent(BinlogEvent event) {
        try {
            System.out.println(JsonUtil.toString(event));
            System.out.println();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
});
connector.startAndAwait();
connector.shutdown();
```

## binlog 文件解析

```
FileConnector connector = new FileConnector("/Users/stony/Downloads/my-bin.000032");

connector.setChecksumType(ChecksumType.CRC32);
//自定义过滤
connector.addFilter(new Filter() {
    @Override
    public boolean test(BinlogEvent event) {
        BinlogEvent.Event myEvent = event.getEvent();
        if ((myEvent instanceof RowsEvent) || (myEvent instanceof TableMapEvent)) {
            String dbName;
            String tName;
            if (myEvent instanceof RowsEvent) {
                dbName = ((RowsEvent) myEvent).getSchema();
                tName = ((RowsEvent) myEvent).getTableName();
            } else {
                dbName = ((TableMapEvent) myEvent).getSchema();
                tName = ((TableMapEvent) myEvent).getTable();
            }
            if (dbName != null && "employees".equals(dbName)) {
                if (tName != null && "employees".equals(tName)) {
                    return true;
                }
            }
        }
        return false;
    }
});
connector.registerListener(event -> {
    try {
        System.out.println(event);
    } catch (Exception e) {
        e.printStackTrace();
    }
});
connector.startAndAwait();
connector.shutdown();
```



# mysql 协议文档


https://segmentfault.com/a/1190000038549577

