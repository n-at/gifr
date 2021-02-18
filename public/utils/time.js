export default {

    formatTime(time) {
        const intSeconds = Math.trunc(time);
        const minutes = Math.trunc(intSeconds / 60) % 60;
        const hours = Math.trunc(intSeconds / 3600);
        const frac = time - intSeconds;
        const seconds = intSeconds % 60 + frac.toFixed(3);
        return `${hours < 10 ? '0'+hours : hours}:${minutes < 10 ? '0'+minutes : minutes}:${seconds < 10 ? '0'+seconds : seconds}`;
    },

};
