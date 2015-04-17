# noprobLAN Cashless (npl-cash)

We use this tool at noprobLAN for managing the badges of the visitors/users. There's one central database to which the users balances and orders are synchronized (_MySQL_, _JPA_). The tool has to be configured by [jpa xml](https://github.com/noproblan/npl-cash/blob/master/src/META-INF/persistence.xml) or by code.

## Badge & Money
The visitors of the event receive a badge containing an RFID which is allocated to a registered user in this tool. At the noprobLAN event theses registrations are synched directly with the noprobLAN homepage. Is also possible to use a CSV or a textfile (_CsvUserProvider_ or _TextfileUserProvider_).

![][users]

A users money balance can be edited. That's done by double click onto the current balance. That's maybe dangerous because the editing person must calculate the new balance still by itself.

## Kitchen
The staff in the kitchen can create new available products which then can be bought by the users at the listed price. Currently it's not possible to delete products (but they can be renamed). You can create barcodes for the IDs of the products (PDF)

![][kitchen]

## Bar
At the last tab users can order products. The IDs of the product have to be typed in followed every time by enter. If you type in a users RFID the list of product is ordered. In this example the RFIDs have been prefixed with a *G* by the scanner.

![][bar]

After a successful order you can see a bill including the amount which was withdrawn from the users account and what is remaining on it.

![][bill]


[users]: https://raw.githubusercontent.com/noproblan/npl-cash/master/docs/screenshot-1.png
[kitchen]: https://raw.githubusercontent.com/noproblan/npl-cash/master/docs/screenshot-2.png
[bar]: https://raw.githubusercontent.com/noproblan/npl-cash/master/docs/screenshot-3.png
[bill]: https://raw.githubusercontent.com/noproblan/npl-cash/master/docs/screenshot-4.png
