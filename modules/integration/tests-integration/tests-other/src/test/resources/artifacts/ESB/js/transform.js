function transform(mc) {
    payload = mc.getPayloadJSON();
    results = payload.results;
    var response = new Array();
    for (i = 0; i < results.length; ++i) {
        location_object = results[i];
        l = new Object();
        l.name = location_object.name;
        l.tags = location_object.types;
        l.id = "ID:" + (location_object.id);
        response[i] = l;
    }
    mc.setPayloadJSON(response);
}
