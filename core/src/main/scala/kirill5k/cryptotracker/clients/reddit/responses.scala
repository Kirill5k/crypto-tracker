package kirill5k.cryptotracker.clients.reddit

import kirill5k.cryptotracker.domain.Subreddit

private[reddit] object responses {

  final case class PushshiftSubmission(
      id: String,
      created_utc: Long,
      retrieved_on: Long,
      full_link: String,
      permalink: String,
      subreddit: Subreddit,
      title: String
  )

  final case class PushshiftSubmissionsResponse(
      data: List[PushshiftSubmission]
  )

  final case class GummysearchSubmission(
      title: String,
      url: String,
      timestamp_utc: Double,
      subreddit_name: String,
      link: String
  )

  final case class GummysearchSubmissionsResponse(
      results: List[GummysearchSubmission]
  )
}
