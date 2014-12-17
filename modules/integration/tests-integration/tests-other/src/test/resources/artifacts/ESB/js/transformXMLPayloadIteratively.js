function transform(mc) {
    payload = mc.getPayloadJSON();
    results = payload.results;
    var response = <locations/>;
    for (i = 0; i < results.length; ++i) {
        var elem = results[i];
        response.locations += <location>
            <id>{elem.id}</id>
            <name>{elem.name}</name>
            <tags>{elem.types}</tags>
        </location>
    }
    mc.setPayloadXML(response);
}
