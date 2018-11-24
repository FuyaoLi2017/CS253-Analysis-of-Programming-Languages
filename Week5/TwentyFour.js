 /**
 * @file "closed map" programming style
 * @author Fuyao Li
 * @date 11/3/2018
 */

let fs = require("fs");

function TFQuarantine(func) {
    this._funcs = [func];
    
    this.bind = function(func) {
        this._funcs.push(func);
        return this;
    }
    
    this.execute = () => {
        guard_callable = (v) => {
            if (typeof v === 'function')
                return v();
            else
                return v;
        };
        value = () => {
        };
        this._funcs.forEach((func) => {
            value = func(guard_callable(value));
        });
        if (guard_callable(value))
            console.log(guard_callable(value));
    }
}

function get_input(arg) {
    function _f() {
        return process.argv[2];
    }
    return _f;
}

function extract_words(filePath) {
    function _f() {
        var file = fs.readFileSync(filePath, 'utf8');
        var words = file.toString();
        words = words.replace(/[^a-zA-Z\\d\\s]/g, ' ');
        return words.toLowerCase().split(' ');
    }
    return _f;
}

function remove_stop_words(word_list) {
    function _f() {
        let stop_words = (fs.readFileSync("../stop_words.txt") + "").split(',');
        let result = [];
        word_list.forEach((word) => {
            if (stop_words.indexOf(word) === -1 && word.length >= 2) {
                result.push(word);
            }
        });
        // return a list without stop_words and all words' length is longer than 2 characters.
        return result;
    }
    return _f
}

function frequencies(word_list) {
    function _f() {
        let word_freq = {};
        word_list.forEach((word) => {
            // count the frequency of each word
            if (word_freq[word] !== undefined)
                word_freq[word] += 1;
            else
                word_freq[word] = 1;
        });
        return word_freq;
    }
    return _f;
}

function sort(word_freq) {
    
    function _f() {
        var element = Object.keys(word_freq).map(function (key) {
            return [key, word_freq[key]];
        });
        element.sort((key1, key2) => {return key2[1] - key1[1];});
        return element;
    }
    return _f;
}

function top25_freqs(word_freqs) {
    function _f() {
        return word_freqs.slice(0,25).forEach((element) => {console.log(element[0], ' - ', element[1]);})
    }
    return _f;
}

new TFQuarantine(get_input)
    .bind(extract_words)
    .bind(remove_stop_words)
    .bind(frequencies)
    .bind(sort)
    .bind(top25_freqs)
    .execute();






