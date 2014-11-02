I used three fields, 'title', 'manufacturer' and 'price'. 'title' and 'manufacturer' used the standard
string comparison function. With 'price', I added a custom comparison function that returned 1 (TRUE) if the prices
were within <= 10% difference.