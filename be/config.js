// Make sure environment variables are set.
module.exports = {
    'secretKey': process.env.SECRET_KEY,
    'refreshKey': process.env.REFRESH_KEY,
    'mongoUrl' : process.env.MONGO_URL
};