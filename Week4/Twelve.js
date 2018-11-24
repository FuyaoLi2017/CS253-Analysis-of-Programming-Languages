
/**
 * @file "closed map" programming style
 * @author Fuyao Li
 * @date 10/27/2018
 */
var fs = require('fs');

function extract_words(obj, filePath) {
    var file = fs.readFileSync(filePath, 'utf8');
    var words = file.toString();
    words = words.replace(/[^a-zA-Z\\d\\s]/g, ' ');
    obj['data'] = words.toLowerCase().split(' ');
}

function load_stop_words(obj) {
    var stop_words_file = fs.readFileSync('../stop_words.txt', 'utf8');
    var stopWords = stop_words_file.toString();
    obj['stop_words'] = stopWords.split(',');
}

function increment_count(obj, word) {
    if (obj['freqs'][word] == null) {obj['freqs'][word] = 1;}
    else {obj['freqs'][word] += 1;}
}

var data_storage_obj = {
    'data':[],
    'init':(filePath) => extract_words(data_storage_obj, filePath),
    'words': function (){return this['data']}
};



var stop_words_obj = {
    'stop_words': [],
    'init': function() {load_stop_words(stop_words_obj)},
    'is_stop_word': function(word) {return stop_words_obj['stop_words'].includes(word);}
};

var word_freqs_obj = {
    'freqs':{},
    'increment_count': function(word) {increment_count(word_freqs_obj, word)},
    'sorted': function() {
        return Object.keys(word_freqs_obj['freqs']).map(function (key) {
            return [key, word_freqs_obj['freqs'][key]];
        }).sort((key1, key2) => {return key2[1] - key1[1];});
    },
    'top25': (output) => output.slice(0,25).forEach((element) => {console.log(element[0], ' - ', element[1]);})
};


// load file
data_storage_obj['init'](process.argv[2]);
stop_words_obj['init']();

data_storage_obj['words']().forEach(function (word) {
    if (!stop_words_obj['is_stop_word'](word) && word.length > 1)
        word_freqs_obj['increment_count'](word);

});

word_freqs_obj['top25'](word_freqs_obj['sorted']());
