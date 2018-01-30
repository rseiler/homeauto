(function displayDateTime() {
    'use strict';

    var isKindle = window.navigator.userAgent.indexOf("armv7l") > 0;
    var weekday = ['So', 'Mo', 'Di', 'Mi', 'Do', 'Fr', 'Sa'];

    printDateTime();
    setTimeout(function () {
        printDateTime();
        setInterval(printDateTime, 60000);
    }, (60 - new Date().getSeconds()) * 1000);

    function printDateTime() {
        document.getElementById("time").innerHTML = timeFormat();
        document.getElementById("date").innerHTML = dateFormat();
    }

    function dateFormat() {
        var date = new Date();
        return weekday[date.getDay()] + ' ' +
            pad2(date.getDate()) + '.' +
            pad2(date.getMonth() + 1);
    }

    function timeFormat() {
        var date = new Date();
        return pad2(date.getHours() + (isKindle ? 1 : 0)) + ':' +
            pad2(date.getMinutes());
    }

    function pad2(number) {
        return (number < 10 ? '0' : '') + number;
    }
})();

(function updateWeather() {
    'use strict';
    var date = new Date();
    var nextFullHour = (61 - date.getMinutes()) * 60 * 1000;

    setTimeout(function () {
        reloadWeather();
        setInterval(reloadWeather, 3600 * 1000);
    }, nextFullHour);

    function reloadWeather() {
        var weatherNode = document.getElementById("weather");
        weatherNode.style.backgroundColor = 'black';

        setTimeout(function () {
            var request = new XMLHttpRequest();
            request.onreadystatechange = function () {
                if (request.readyState === XMLHttpRequest.DONE && request.status === 200) {
                    weatherNode.style.backgroundColor = 'white';
                    weatherNode.innerHTML = request.responseText.replace(/<div(.*?)>/, '').replace(/<\/div>/, '').trim();
                }
            };
            request.open('GET', '/fragment/weather', true);
            request.send();
        }, 1000);
    }
})();

(function updateCalendar() {
    'use strict';
    setInterval(function () {
        if (new Date().getHours() === 1) {
            var weatherNode = document.getElementById("calendar");
            var request = new XMLHttpRequest();
            request.onreadystatechange = function () {
                if (request.readyState === XMLHttpRequest.DONE && request.status === 200) {
                    weatherNode.innerHTML = request.responseText.replace(/<div(.*?)>/, '').replace(/<\/div>/, '').trim();
                }
            };
            request.open('GET', '/fragment/calendar', true);
            request.send();
        }
    }, 3600 * 1000);
})();

(function resetDisplay() {
    'use strict';

    setInterval(function () {
        if (new Date().getHours() === 4) {
            document.getElementById('body').style.backgroundColor = 'black';

            setTimeout(function () {
                document.getElementById('body').style.backgroundColor = 'white';
            }, 5000);
        }
    }, 3600 * 1000);
})();