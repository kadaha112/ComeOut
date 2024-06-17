package data

data class MovieResponse(val boxOfficeResult: BoxOfficeResult)

data class BoxOfficeResult(val dailyBoxOfficeList: List<Movie>)

data class Movie(
    val rank: String,
    val movieNm: String,
    val openDt: String,
    val audiAcc: String // 누적 관객수
)

