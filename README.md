# Restaurant Helper

## Opis:
"Restaurant Helper" to aplikacja na system Android, która ma pomóc w zarządzaniu restauracjami w których występuje system obsługi kelnerskiej. Program będzie posiadał system logowania jako administrator, kucharz i kelner. Konta dodawać i usuwać może tylko administrator. W domyśle jest to kierownik restauracji. Kelnerzy i kucharze mogą się logować oraz dodawać i usuwać zamówienia. Administrator dodatkowo może tworzyć i edytować tabelę zawierającą MENU. Dodawać potrawy i ich ceny. 

## Schemat działania:
Administrator rozpoczyna dzień roboczy w aplikacji. Od teraz pozostały presonel może przyjmować i realizować zamówienia. W momencie kiedy administrator zakończy dzień roboczy, pracownicy nie mają już dostępu do zamówień, a w bazie danych zostaje zapisany dzień pracy z rozliczeniem (przychód , ilość zamówień). Kelner posiada 3 funkcje: dodaj zamówienie, usuń zamówienie( w przypadku pomyłki, jeśli zamówienie zostało wydane kelner nie może go usunąć) i opłacono ( po zatwierdzeniu, zamówinie jest całkowicie zrealizowane i zostaje przekazane do rozliczenia). Kelner wybiera z dostępnego menu zamówione potrawy i zaznacza stolik. Kucharz ma tylko jedną opcje , jest to "wydanie zamówienia", które ma działanie opisane powyżej. W momencie kiedy kucharz wyda zamówienie. Kelner otrzymuje powiadomienie. Analogicznie, gdy kelner złoży zamówienie, kucharze otrzymują stosowne powiadomienia. Jeśli w danym momencie, skończą się jakieś produkty. Kucharze muszą niezwłocznie powiadomić kierownika, który oznaczy potrawy niemożliwe do przygotowania w aplikacji. Dzięki czemu kelnerzy nie będą mogli składać zamówień zawierających niedostępne produkty. Ze względów bezpieczeństwa tylko administrator może edytować ważne tabele. Komu zostanie przydzielone to konto, zależy już wyłącznie od woli właściciela restauracji.

## Wykorzystane technologie:
Aplikacja będzie działała na telefonach z systemem android. Tworzona będzie przy pomocy "Android Studio". Natomiast baza danych z której będzie korzystać aplikacja postawiona jest już na wykupionym serwerze "Unicloud" na którym zainstalowany jest "MySQL". Do połączenia z nią użyjemy biblioteki "MySQL Client" z Android Studio. Aplikacja będzie miała domyślny wygląd androidowego programu. Jeśli uda nam się zmieścić w planowanym czasie to spróbujemy "dopieścić" wygląd dodając swoje grafiki.

## Planowany wygląd aplikacji:
### Okno startowe

![main window](https://user-images.githubusercontent.com/16230307/37730535-44ad9c10-2d40-11e8-82a7-9401e9d55a39.png)
