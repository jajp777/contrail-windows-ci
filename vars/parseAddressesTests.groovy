def call() {
    echo "Running parseAddressesTests"

    def inventory_file = "test_inventory"
    def content = """\
        504-wintb01;1.2.3.4;uuid
        504-wintb02;1.2.3.5;uuid
        504-controller;1.2.3.6,0.0.0.0;uuid
        """.stripIndent()

    writeFile file: inventory_file, text: content

    def testbeds = parseTestbedAddresses(inventory_file)
    assert testbeds == ["1.2.3.4", "1.2.3.5"]

    def controller = parseControllerAddress(inventory_file)
    assert controller == ["1.2.3.6"]

    echo "OK"
}
