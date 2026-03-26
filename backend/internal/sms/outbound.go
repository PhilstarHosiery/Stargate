package sms

import (
	"bytes"
	"encoding/json"
	"fmt"
	"net/http"
	"time"
)

// OutboundClient sends SMS messages via the SMS Gate API.
type OutboundClient struct {
	gateURL string
	apiKey  string
	http    *http.Client
}

// NewOutboundClient creates an OutboundClient for the given gateway base URL and API key.
func NewOutboundClient(gateURL, apiKey string) *OutboundClient {
	return &OutboundClient{
		gateURL: gateURL,
		apiKey:  apiKey,
		http: &http.Client{
			Timeout: 15 * time.Second,
		},
	}
}

type outboundPayload struct {
	PhoneNumbers []string        `json:"phoneNumbers"`
	TextMessage  outboundMessage `json:"textMessage"`
	SimNumber    int             `json:"simNumber"`
}

type outboundMessage struct {
	Text string `json:"text"`
}

// Send POSTs an SMS via the SMS Gate API. sim should be 1 (Globe) or 2 (Smart).
func (c *OutboundClient) Send(to string, sim int, message string) error {
	payload := outboundPayload{
		PhoneNumbers: []string{to},
		TextMessage:  outboundMessage{Text: message},
		SimNumber:    sim,
	}

	body, err := json.Marshal(payload)
	if err != nil {
		return fmt.Errorf("sms: marshal payload: %w", err)
	}

	req, err := http.NewRequest(http.MethodPost, c.gateURL+"/message", bytes.NewReader(body))
	if err != nil {
		return fmt.Errorf("sms: create request: %w", err)
	}
	req.Header.Set("Content-Type", "application/json")
	if c.apiKey != "" {
		req.Header.Set("Authorization", "Bearer "+c.apiKey)
	}

	resp, err := c.http.Do(req)
	if err != nil {
		return fmt.Errorf("sms: send request: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		return fmt.Errorf("sms: gateway returned status %d", resp.StatusCode)
	}

	return nil
}
