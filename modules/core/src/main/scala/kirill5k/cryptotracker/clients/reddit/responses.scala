package kirill5k.cryptotracker.clients.reddit

import kirill5k.cryptotracker.domain.Subreddit

private[reddit] object responses {

  final case class Submission(
      id: String,
      created_utc: Long,
      retrieved_on: Long,
      full_link: String,
      permalink: String,
      subreddit: Subreddit,
      title: String
  )

  final case class RedditSubmissionsResponse(
      data: List[Submission]
  )
}